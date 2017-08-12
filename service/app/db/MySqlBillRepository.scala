package db

import javax.inject.Inject

import domain.{Bill, BillState}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.BillRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future


class MySqlBillRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends BillRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(bill: Bill): Future[Bill] = {
    val items = bills returning bills.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += bill)
  }

  override def retrieveOne(id: Long): Future[Option[Bill]] = {
    db.run(bills.filter(_.id === id).result.headOption)
  }

  override def retrieveAll(offset: Int, limit: Int): Future[Seq[Bill]] = {
    db.run(bills.drop(offset).take(limit).result)
  }

  override def retrieveByUser(userId: Long, offset: Int, limit: Int): Future[Seq[Bill]] = {
    db.run(bills.filter(_.customer === userId).drop(offset).take(limit).result)
  }

  override def setState(id: Long, state: BillState): Future[Int] = {
    val q = for {bill <- bills if bill.id === id} yield bill.state
    db.run(q.update(state))
  }

  override def enlargeAmount(id: Long, amount: Double): Future[Int] = {
    val query =
      sql"""
           UPDATE
              bills
           SET
              amount = amount + $amount
           WHERE id = $id;
        """.as[Int].head
    db.run(query)
  }

  override def modify(id: Long, bill: Bill): Future[Int] = {
    val q = for {b <- bills if b.id === id} yield (b.amount, b.discount, b.discountAmount, b.pointsGained)
    db.run(q.update((bill.amount, bill.discount, bill.discountAmount, bill.pointsGained)))
  }

}
