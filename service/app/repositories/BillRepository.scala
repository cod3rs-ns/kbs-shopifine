package repositories

import domain.{Bill, BillState}

import scala.concurrent.Future

trait BillRepository {

  def save(bill: Bill): Future[Bill]

  def retrieveOne(id: Long): Future[Option[Bill]]

  def retrieveAll(offset: Int, limit: Int): Future[Seq[Bill]]

  def retrieveByUser(userId: Long, offset: Int, limit: Int): Future[Seq[Bill]]

  def setState(id: Long, state: BillState): Future[Int]

  def enlargeAmount(id: Long, amount: Double): Future[Int]

}
