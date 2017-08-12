package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.CollectionLinks
import hateoas.bill_discounts.BillDiscountCollectionResponse
import hateoas.bill_item_discounts.BillItemDiscountCollectionResponse
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.{BillDiscountRepository, BillItemDiscountRepository}

import scala.concurrent.ExecutionContext

@Singleton
class Discounts @Inject()(billDiscounts: BillDiscountRepository,
                          billItemDiscounts: BillItemDiscountRepository,
                          secure: SecuredAuthenticator)
                         (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles.Customer

  def retrieveBillDiscounts(userId: Long, billId: Long, offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    // FIXME Not found Bill
    billDiscounts.retrieveAll(billId, offset, limit).map(discounts => {
      val self = routes.Discounts.retrieveBillDiscounts(userId, billId, offset, limit).absoluteURL()
      val next = if (limit == discounts.length) Some(routes.Discounts.retrieveBillDiscounts(userId, billId, offset + limit, limit).absoluteURL()) else None

      Ok(Json.toJson(
        BillDiscountCollectionResponse.fromDomain(discounts, CollectionLinks(self = self, next = next), userId)
      ))
    })
  }

  def retrieveBillItemDiscounts(userId: Long, billId: Long, billItemId: Long, offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    // FIXME Not found Bill or Bill Item
    billItemDiscounts.retrieveAll(billId, offset, limit).map(discounts => {
      val self = routes.Discounts.retrieveBillItemDiscounts(userId, billId, billItemId, offset, limit).absoluteURL()
      val next = if (limit == discounts.length) Some(routes.Discounts.retrieveBillItemDiscounts(userId, billId, billItemId, offset + limit, limit).absoluteURL()) else None

      Ok(Json.toJson(
        BillItemDiscountCollectionResponse.fromDomain(discounts, CollectionLinks(self = self, next = next), userId, billId)
      ))
    })
  }

}
