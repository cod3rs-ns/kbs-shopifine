package ws

import domain.BillState
import org.joda.time.DateTime

object Messages {

  trait BaseNotification {
    def `type`: NotificationType
  }

  case class OrderStateChanged(
      override val `type`: NotificationType = NotificationType.ORDER_STATUS_CHANGED,
      billId: Long,
      state: BillState
  ) extends BaseNotification

  case class ActionDiscountCreated(
      override val `type`: NotificationType = NotificationType.ACTION_DISCOUNT_CREATED,
      discountId: Long,
      name: String,
      from: DateTime,
      to: DateTime,
      discount: Double
  ) extends BaseNotification

  case class OrderAddressChanged(
      override val `type`: NotificationType = NotificationType.ORDER_ADDRESS_CHANGED,
      orderId: Long,
      address: String,
      latitude: Double,
      longitude: Double
  ) extends BaseNotification

  case class OrderInRadius(
      override val `type`: NotificationType = NotificationType.ORDER_IN_RADIUS,
      orderId: Long,
      distance: Double
  ) extends BaseNotification

  case class ProductPriceChanged(
      override val `type`: NotificationType = NotificationType.PRODUCT_PRICE_CHANGED,
      productId: Long,
      name: String,
      imageUrl: String,
      categoryId: Long,
      price: Double,
      quantity: Long
  ) extends BaseNotification

  case class OneProductLeft(
      override val `type`: NotificationType = NotificationType.ONE_PRODUCT_LEFT,
      productId: Long,
      name: String,
      imageUrl: String,
      categoryId: Long,
      price: Double
  ) extends BaseNotification
}
