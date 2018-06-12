package ws

import domain.BillState

object Messages {

  trait BaseNotification {
    def `type`: NotificationType
  }

  case class OrderStateChanged(
      override val `type`: NotificationType = NotificationType.ORDER_STATUS_CHANGED,
      orderId: Long,
      state: BillState,
      createdAt: String,
      pointsGained: Long,
      pointsSpent: Long,
      amount: Double,
      discount: Double,
      totalItems: Long,
      address: String,
      latitude: Double,
      longitude: Double
  ) extends BaseNotification

  case class ActionDiscountCreated(
      override val `type`: NotificationType = NotificationType.ACTION_DISCOUNT_CREATED,
      discountId: Long,
      name: String,
      from: String,
      to: String,
      discount: Double
  ) extends BaseNotification

  case class OrderAddressChanged(
      override val `type`: NotificationType = NotificationType.ORDER_ADDRESS_CHANGED,
      orderId: Long,
      state: BillState,
      address: String,
      latitude: Double,
      longitude: Double,
      createdAt: String,
      pointsGained: Long,
      pointsSpent: Long,
      amount: Double,
      discount: Double,
      totalItems: Long) extends BaseNotification

  case class OrderInRadius(
      override val `type`: NotificationType = NotificationType.ORDER_IN_RADIUS,
      orderId: Long,
      state: BillState,
      distance: Double,
      address: String,
      latitude: Double,
      longitude: Double,
      createdAt: String,
      pointsGained: Long,
      pointsSpent: Long,
      amount: Double,
      discount: Double,
      totalItems: Long
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
