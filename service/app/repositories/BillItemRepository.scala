package repositories

import domain.BillItem

import scala.concurrent.Future

trait BillItemRepository {

  def save(billItem: BillItem): Future[BillItem]

  def retrieveByBill(billId: Long, offset: Int, limit: Int): Future[Seq[BillItem]]

}
