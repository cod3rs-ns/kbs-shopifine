package ws

import com.google.inject.Inject
import domain.Bill
import play.api.libs.json.Json
import ws.MessageFormat._
import ws.Messages.BillStateChanged
import ws.NotificationBus.Notification

class NotificationPublisher @Inject()(notificationBus: NotificationBus) {

  def orderStatusChanged(bill: Bill): Unit = {
    val message = Json
      .toJson(BillStateChanged(billId = bill.customerId, state = bill.state))
      .toString

    notificationBus.publish(Notification(bill.customerId, message))
  }

}
