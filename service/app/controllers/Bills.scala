package controllers

import javax.inject.Singleton

import bills.{BillCollectionResponse, BillRequest, BillResponse}
import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import domain.BillState
import external.DroolsProxy
import hateoas.bill_items.{BillItemCollectionResponse, BillItemRequest, BillItemResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.{BillDiscountRepository, BillItemDiscountRepository, BillItemRepository}
import services.{BillService, ProductService}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Bills @Inject()(bills: BillService,
                      billDiscounts: BillDiscountRepository,
                      billItems: BillItemRepository,
                      billItemDiscounts: BillItemDiscountRepository,
                      products: ProductService,
                      drools: DroolsProxy,
                      secure: SecuredAuthenticator)
                     (implicit val ec: ExecutionContext) extends Controller {

  import Bills.FilterStartsWith
  import hateoas.JsonApi._
  import secure.Roles._

  def create(userId: Long): Action[JsValue] = secure.AuthWith(Seq(Customer)).async(parse.json) { implicit request =>
    if (request.user.isDefined && request.user.get.id.get != userId) {
      Future.successful(Forbidden(Json.toJson(
        ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
      )))
    }
    else {
      request.body.validate[BillRequest].fold(
        _ => Future.successful(BadRequest(Json.toJson(
          ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
        ))),

        spec => bills.save(spec.toDomain).map(bill =>
          Accepted(Json.toJson(
            BillResponse.fromDomain(bill)
          ))
        )
      )
    }
  }

  def retrieveAllByUser(userId: Long, offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    if (request.user.isDefined && request.user.get.id.get != userId) {
      Future.successful(Forbidden(Json.toJson(
        ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
      )))
    }
    else {
      bills.retrieveByUser(userId, offset, limit).map(result => {
        val self = routes.Bills.retrieveAllByUser(userId, offset, limit).absoluteURL()
        val next = if (result.size == limit) Some(routes.Bills.retrieveAllByUser(userId, offset, limit).absoluteURL()) else None

        Ok(Json.toJson(
          BillCollectionResponse.fromDomain(result, CollectionLinks(self = self, next = next))
        ))
      })
    }
  }

  def retrieveOneByUser(userId: Long, billId: Long): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    if (request.user.isDefined && request.user.get.id.get != userId) {
      Future.successful(Forbidden(Json.toJson(
        ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
      )))
    }
    else {
      bills.retrieveOne(billId).flatMap {
        case Some(bill) => Future.successful(Ok(Json.toJson(BillResponse.fromDomain(bill))))

        case None => Future.successful(NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Bill $billId doesn't exist!")))
        )))
      }
    }
  }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Salesman)).async { implicit request =>
    val filters = request.queryString.filterKeys(_.startsWith(FilterStartsWith)).map {
      case (k, v) => k.substring(FilterStartsWith.length, k.length - 1) -> v.mkString
    }

    bills.retrieveBillsWithFilters(filters, offset, limit).map(result => {
      val self = routes.Bills.retrieveAll(offset, limit).absoluteURL()
      val next = if (result.size == limit) Some(routes.Bills.retrieveAll(offset, limit).absoluteURL()) else None

      Ok(Json.toJson(
        BillCollectionResponse.fromDomain(result, CollectionLinks(self = self, next = next))
      ))
    })
  }

  def changeState(id: Long, state: String): Action[AnyContent] = secure.AuthWith(Seq(Salesman)).async {
    try {
      val toState = BillState.valueOf(state.toUpperCase)
      bills.setState(id, toState).flatMap {
        case 1 =>
          bills.retrieveOne(id).map(bill =>
            Ok(Json.toJson(BillResponse.fromDomain(bill.get)))
          )

        case -1 =>
          Future.successful(BadRequest(Json.toJson(
            ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, s"Insufficiently Products for Bill Accepting.")))
          )))

        case -2 =>
          Future.successful(BadRequest(Json.toJson(
            ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, s"Customer doesn't have enough points.")))
          )))

        case _ =>
          Future.successful(NotFound(Json.toJson(
            ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Bill $id doesn't exist!")))
          )))
      }
    }
    catch {
      case e: IllegalArgumentException =>
        Future.successful(BadRequest(Json.toJson(
          ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, e.getMessage)))
        )))
    }
  }

  def addBillItem(userId: Long, billId: Long): Action[JsValue] = secure.AuthWith(Seq(Customer)).async(parse.json) { implicit request =>
    if (request.user.isDefined && request.user.get.id.get != userId) {
      Future.successful(Forbidden(Json.toJson(
        ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
      )))
    }
    else {
      request.body.validate[BillItemRequest].fold(
        _ => Future.successful(BadRequest(Json.toJson(
          ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
        ))),

        spec => {
          bills.retrieveOne(billId) flatMap {
            case Some(bill) =>
              drools.calculateBillItemPriceAndDiscounts(userId, spec).flatMap(bonuses =>
                billItems.save(
                  spec.toDomain.copy(
                    price = bonuses.price,
                    amount = bonuses.amount,
                    discount = bonuses.discount,
                    discountAmount = bonuses.discountAmount
                  )
                ).flatMap(billItem => {
                  // Store all retrieved Discounts for Bill Item
                  bonuses.discounts.foreach(d => billItemDiscounts.save(d.toBillItemDiscount(billItem)))

                  // Update when Product is bought last time
                  products.boughtNow(billItem.productId)

                  // Update total amount of Bill
                  bills.enlargeAmount(billId, bonuses.amount)
                    .flatMap(_ => {
                      // If item is last on the Bill trigger Bill price and discounts calculating
                      if (billItem.ordinal == bill.totalItems) {
                        drools.calculateBillPriceAndDiscounts(userId, billId).map(bonuses => {
                          // Store all retrieved Discounts for Bill
                          bonuses.discounts.foreach(d => billDiscounts.save(d.toBillDiscount(bill)))

                          // Update stuffs related to Bill calculated price
                          bills.updateBillCalculation(
                            bill.copy(
                              amount = bonuses.amount,
                              discount = bonuses.discount,
                              discountAmount = bonuses.discountAmount,
                              pointsGained = bonuses.pointsGained
                            )
                          )
                        })
                      }

                      Future.successful(Created(Json.toJson(
                        BillItemResponse.fromDomain(billItem, bill.id.get)
                      )))
                    })
                })
              )

            case None => Future.successful(NotFound(Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Bill $billId doesn't exist!")))
            )))
          }
        }
      )
    }
  }

  def retrieveBillItems(userId: Long, billId: Long, offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    if (request.user.isDefined && request.user.get.id.get != userId) {
      Future.successful(Forbidden(Json.toJson(
        ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
      )))
    }
    else {
      bills.retrieveOne(billId) flatMap {
        case Some(_) => billItems.retrieveByBill(billId, offset, limit).map(result => {
          val self = routes.Bills.retrieveBillItems(userId, billId, offset, limit).absoluteURL()
          val next = if (result.size == limit) Some(routes.Bills.retrieveBillItems(userId, billId, offset, limit).absoluteURL()) else None

          Ok(Json.toJson(
            BillItemCollectionResponse.fromDomain(result, userId, CollectionLinks(self = self, next = next))
          ))
        })

        case None => Future.successful(NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Bill $billId doesn't exist!")))
        )))
      }
    }
  }
}

object Bills {
  val FilterStartsWith = "filter["
}
