package ws

import domain.BillState
import play.api.libs.json._
import ws.Messages.{ActionDiscountCreated, ProductPriceChanged, _}

object MessageFormat {

  implicit val billStateWrites = new Writes[BillState] {
    def writes(o: BillState): JsValue = JsString(o.toString)
  }

  implicit val notificationTypeWrites = new Writes[NotificationType] {
    override def writes(o: NotificationType): JsValue = JsString(o.getName)
  }

  implicit val orderStateChangedF: Writes[OrderStateChanged]   = Json.writes[OrderStateChanged]
  implicit val discountCreatedF: Writes[ActionDiscountCreated] = Json.writes[ActionDiscountCreated]
  implicit val priceChangedF: Writes[ProductPriceChanged]      = Json.writes[ProductPriceChanged]
  implicit val addressChangedF: Writes[OrderAddressChanged]    = Json.writes[OrderAddressChanged]
  implicit val oneProductLeftF: Writes[OneProductLeft]         = Json.writes[OneProductLeft]

}
