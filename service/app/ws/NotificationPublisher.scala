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
      .toJson(
        OrderStateChanged(
          orderId = bill.id.get,
          state = bill.state,
          createdAt = bill.createdAt.toString,
          pointsGained = bill.pointsGained,
          pointsSpent = bill.pointsSpent,
          amount = bill.amount,
          discount = bill.discount,
          totalItems = bill.totalItems,
          address = bill.address.get,
          latitude = bill.latitude.get,
          longitude = bill.longitude.get
        ))
      .toString

    notificationBus.publish(Notification(bill.customerId, message))
  }

  def actionDiscountCreated(actionDiscount: ActionDiscount): Unit = {
    val message = Json
      .toJson(
        ActionDiscountCreated(
          discountId = actionDiscount.id.get,
          name = actionDiscount.name,
          from = actionDiscount.from.toString,
          to = actionDiscount.to.toString,
          discount = actionDiscount.discount
        ))
      .toString

    userRepository.getUserIds.runForeach { userId =>
      notificationBus.publish(Notification(userId, message))
    }
  }

  def orderAddressChanged(bill: Bill): Unit = {
    val message = Json
      .toJson(
        OrderAddressChanged(
          orderId = bill.id.get,
          state = bill.state,
          createdAt = bill.createdAt.toString,
          pointsGained = bill.pointsGained,
          pointsSpent = bill.pointsSpent,
          amount = bill.amount,
          discount = bill.discount,
          totalItems = bill.totalItems,
          address = bill.address.get,
          latitude = bill.latitude.get,
          longitude = bill.longitude.get
        ))
      .toString

    notificationBus.publish(Notification(bill.customerId, message))
  }

  def orderInRadius(bill: Bill, distance: Double): Unit = {
    val message = Json
      .toJson(
        OrderInRadius(
          orderId = bill.id.get,
          state = bill.state,
          distance = distance,
          createdAt = bill.createdAt.toString,
          pointsGained = bill.pointsGained,
          pointsSpent = bill.pointsSpent,
          amount = bill.amount,
          discount = bill.discount,
          totalItems = bill.totalItems,
          address = bill.address.get,
          latitude = bill.latitude.get,
          longitude = bill.longitude.get
        ))
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
