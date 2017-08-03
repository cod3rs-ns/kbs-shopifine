package controllers

import javax.inject.Singleton

import bills.{BillCollectionResponse, BillRequest, BillResponse}
import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import domain.BillState
import hateoas.bill_items.{BillItemCollectionResponse, BillItemRequest, BillItemResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.BillItemRepository
import services.BillService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Bills @Inject()(bills: BillService, billItems: BillItemRepository, secure: SecuredAuthenticator)
                     (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles._

  def create(userId: Long): Action[JsValue] = secure.AuthWith(Seq(Customer)).async(parse.json) { implicit request =>
    if (request.user.id.get != userId) {
      Future.successful(Forbidden(Json.toJson(
        ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
      )))
    }
    else {
      request.body.validate[BillRequest].fold(
        failures => Future.successful(BadRequest(Json.toJson(
          ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
        ))),

        spec => {
          bills.save(spec.toDomain).map(bill =>
            Accepted(Json.toJson(
              BillResponse.fromDomain(bill)
            ))
          )
        }
      )
    }
  }

  def retrieveAllByUser(userId: Long, offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    if (request.user.id.get != userId) {
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
    if (request.user.id.get != userId) {
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
    val filters = request.queryString.filterKeys(_.startsWith("filter[")).map { case (k, v) =>
      k.substring(7, k.length - 1) -> v.mkString
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
      bills.setState(id, toState).flatMap(affected => {
        if (affected > 0) {
          bills.retrieveOne(id).map(bill =>
            Ok(Json.toJson(BillResponse.fromDomain(bill.get)))
          )
        }
        else {
          Future.successful(NotFound(Json.toJson(
            ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Bill $id doesn't exist!")))
          )))
        }
      }
      )
    }
    catch {
      case e: IllegalArgumentException =>
        Future.successful(BadRequest(Json.toJson(
          ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, e.getMessage)))
        )))
    }
  }

  def addBillItem(userId: Long, billId: Long): Action[JsValue] = secure.AuthWith(Seq(Customer)).async(parse.json) { implicit request =>
    if (request.user.id.get != userId) {
      Future.successful(Forbidden(Json.toJson(
        ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
      )))
    }
    else {
      request.body.validate[BillItemRequest].fold(
        failures => Future.successful(BadRequest(Json.toJson(
          ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
        ))),

        spec => {
          bills.retrieveOne(billId) flatMap {
            case Some(bill) =>
              billItems.save(spec.toDomain).map(billItem =>
                Created(Json.toJson(
                  BillItemResponse.fromDomain(billItem, bill.id.get)
                ))
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
    if (request.user.id.get != userId) {
      Future.successful(Forbidden(Json.toJson(
        ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
      )))
    }
    else {
      bills.retrieveOne(billId) flatMap {
        case Some(bill) =>
          billItems.retrieveByBill(billId, offset, limit).map(result => {
            val self = routes.Bills.retrieveBillItems(userId, billId, offset, limit).absoluteURL()
            val next = if (result.size == limit) Some(routes.Bills.retrieveBillItems(userId, billId, offset, limit).absoluteURL()) else None

            Ok(Json.toJson(
              BillItemCollectionResponse.fromDomain(result, bill.id.get, CollectionLinks(self = self, next = next))
            ))
          })

        case None => Future.successful(NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Bill $billId doesn't exist!")))
        )))
      }
    }
  }

}
