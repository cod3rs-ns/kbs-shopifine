package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.CollectionLinks
import hateoas.bill_discounts.BillDiscountCollectionResponse
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.BillDiscountRepository

import scala.concurrent.ExecutionContext

@Singleton
class Discounts @Inject()(billDiscounts: BillDiscountRepository, secure: SecuredAuthenticator)(implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles.Customer

  def retrieveBillDiscounts(userId: Long, billId: Long, offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async { implicit request =>
    billDiscounts.retrieveAll(billId, offset, limit).map(discounts => {
      val self = routes.Discounts.retrieveBillDiscounts(userId, billId, offset, limit).absoluteURL()
      val next = if (limit == discounts.length) Some(routes.Discounts.retrieveBillDiscounts(userId, billId, offset + limit, limit).absoluteURL()) else None

      Ok(Json.toJson(
        BillDiscountCollectionResponse.fromDomain(discounts, CollectionLinks(self = self, next = next), userId)
      ))
    })
  }

}
