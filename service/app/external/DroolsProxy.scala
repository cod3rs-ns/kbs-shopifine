package external

import javax.inject.Singleton

import com.google.inject.Inject
import hateoas.bill_items.BillItemRequest
import hateoas.drools_service.{BillItemWithDiscountsResponse, BillWithDiscountsResponse, DroolsProductResponseCollection}
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DroolsProxy @Inject()(ws: WSClient, config: Configuration)(implicit val ec: ExecutionContext) {

  import hateoas.JsonApi._

  private val DroolsServiceBaseUrl = config.getString("service.drools.url").getOrElse("N/A")

  def calculateBillPriceAndDiscounts(userId: Long, billId: Long): Future[BillWithDiscountsResponse] = {
    val request = ws.url(s"$DroolsServiceBaseUrl/api/users/$userId/bills/$billId/discounts")
      .get()

    request.map(_.json.as[BillWithDiscountsResponse])
  }

  def calculateBillItemPriceAndDiscounts(item: BillItemRequest): Future[BillItemWithDiscountsResponse] = {
    val request = ws.url(s"$DroolsServiceBaseUrl/api/bill-items/discounts")
      .put(Json.toJson(item))

    request.map(_.json.as[BillItemWithDiscountsResponse])
  }

  def productsOutOfStock: Future[DroolsProductResponseCollection] = {
    val request = ws.url(s"$DroolsServiceBaseUrl/api/products")
      .get()

    request.map(_.json.as[DroolsProductResponseCollection])
  }

}
