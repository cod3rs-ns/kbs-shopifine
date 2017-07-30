package repositories

import domain.BillItemDiscount

import scala.concurrent.Future

trait BillItemDiscountRepository {

  def save(discount: BillItemDiscount): Future[BillItemDiscount]

  def retrieveOne(id: Long): Future[Option[BillItemDiscount]]

  def retrieveAll(itemId: Long, offset: Int, limit: Int): Future[Seq[BillItemDiscount]]

}
