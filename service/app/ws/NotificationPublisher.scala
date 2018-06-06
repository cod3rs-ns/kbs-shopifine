package ws

import akka.stream.Materializer
import com.google.inject.Inject
import domain.{ActionDiscount, Bill, Product}
import play.api.libs.json.Json
import repositories.UserRepository
import ws.MessageFormat._
import ws.NotificationBus.Notification

class NotificationPublisher @Inject()(notificationBus: NotificationBus,
                                      userRepository: UserRepository)(implicit mat: Materializer) {

  import ws.Messages._

  def orderStatusChanged(bill: Bill): Unit = {
    val message = Json
      .toJson(OrderStateChanged(billId = bill.customerId, state = bill.state))
      .toString

    notificationBus.publish(Notification(bill.customerId, message))
  }

  def actionDiscountCreated(actionDiscount: ActionDiscount): Unit = {
    val message = Json
      .toJson(
        ActionDiscountCreated(
          discountId = actionDiscount.id.get,
          name = actionDiscount.name,
          from = actionDiscount.from,
          to = actionDiscount.to,
          discount = actionDiscount.discount
        ))
      .toString

    userRepository.getUserIds.runForeach { userId =>
      notificationBus.publish(Notification(userId, message))
    }
  }

  def orderAddressChanged(bill: Bill, address: String, longitude: Double, latitude: Double): Unit = {
    val message = Json
      .toJson(
        OrderAddressChanged(
          orderId = bill.id.get,
          address = address,
          latitude = latitude,
          longitude = longitude
        ))
      .toString

    notificationBus.publish(Notification(bill.customerId, message))
  }

  def orderInRadius(bill: Bill, distance: Double): Unit = {
    val message = Json
      .toJson(OrderInRadius(orderId = bill.id.get, distance = distance))
      .toString

    notificationBus.publish(Notification(bill.customerId, message))
  }

  def productPriceChanged(product: Product): Unit = {
    val message = Json
      .toJson(
        ProductPriceChanged(
          productId = product.id.get,
          name = product.name,
          imageUrl = product.imageUrl,
          categoryId = product.categoryId,
          price = product.price,
          quantity = product.quantity
        ))
      .toString

    // TODO: only send to user who has that product in his wishlist
    userRepository.getUserIds.runForeach(userId =>
      notificationBus.publish(Notification(userId, message)))
  }

  def oneProductLeft(product: Product): Unit = {
    val message = Json
      .toJson(
        OneProductLeft(
          productId = product.id.get,
          name = product.name,
          imageUrl = product.imageUrl,
          categoryId = product.categoryId,
          price = product.price
        ))
      .toString

    // TODO: only send to user who has that product in his wishlist
    userRepository.getUserIds.runForeach(userId =>
      notificationBus.publish(Notification(userId, message)))
  }
}
