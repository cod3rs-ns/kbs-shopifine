package repositories

import domain.BillDiscount

import scala.concurrent.Future

trait BillDiscountRepository {

  def save(discount: BillDiscount): Future[BillDiscount]

  def retrieveOne(id: Long): Future[Option[BillDiscount]]

  def retrieveAll(billId: Long): Future[Seq[BillDiscount]]

}
