package ws

import domain.BillState

object Messages {

  case class BillStateChanged(billId: Long,
                              `type`: String =
                                NotificationType.ORDER_STATUS_CHANGED.name(),
                              state: BillState)
}
