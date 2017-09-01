package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import hateoas.bill_discounts.BillDiscountCollectionResponse
import hateoas.bill_item_discounts.BillItemDiscountCollectionResponse
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.{BillDiscountRepository, BillItemDiscountRepository}
import services.BillService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Discounts @Inject()(bills: BillService,
                          billDiscounts: BillDiscountRepository,
                          billItemDiscounts: BillItemDiscountRepository,
                          secure: SecuredAuthenticator)
                         (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles.Customer

  def retrieveBillDiscounts(userId: Long, billId: Long, offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    bills.retrieveOne(billId).flatMap {
      case Some(_) =>
        billDiscounts.retrieveAll(billId, offset, limit).map(discounts => {
          val self = routes.Discounts.retrieveBillDiscounts(userId, billId, offset, limit).absoluteURL()
          val next = if (limit == discounts.length) Some(routes.Discounts.retrieveBillDiscounts(userId, billId, offset + limit, limit).absoluteURL()) else None

          Ok(Json.toJson(
            BillDiscountCollectionResponse.fromDomain(discounts, CollectionLinks(self = self, next = next), userId)
          ))
        })

      case None => Future.successful(NotFound(Json.toJson(
        ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Bill $billId doesn't exist!")))
      )))
    }
  }

  def retrieveBillItemDiscounts(userId: Long, billId: Long, billItemId: Long, offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    bills.retrieveOne(billId).flatMap {
      case Some(_) =>
        billItemDiscounts.retrieveAll(billItemId, offset, limit).map(discounts => {
          val self = routes.Discounts.retrieveBillItemDiscounts(userId, billId, billItemId, offset, limit).absoluteURL()
          val next = if (limit == discounts.length) Some(routes.Discounts.retrieveBillItemDiscounts(userId, billId, billItemId, offset + limit, limit).absoluteURL()) else None

          Ok(Json.toJson(
            BillItemDiscountCollectionResponse.fromDomain(discounts, CollectionLinks(self = self, next = next), userId, billId)
          ))
        })

      case None => Future.successful(NotFound(Json.toJson(
        ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Bill $billId doesn't exist!")))
      )))
    }
  }

}
