package db

import javax.inject.Inject

import domain.BillItemDiscount
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.BillItemDiscountRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future


class MySqlBillItemDiscountRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends BillItemDiscountRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(discount: BillItemDiscount): Future[BillItemDiscount] = {
    val items = itemDiscounts returning itemDiscounts.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += discount)
  }

  override def retrieveOne(id: Long): Future[Option[BillItemDiscount]] = {
    db.run(itemDiscounts.filter(_.id === id).result.headOption)
  }

  override def retrieveAll(itemId: Long, offset: Int, limit: Int): Future[Seq[BillItemDiscount]] = {
    db.run(itemDiscounts.filter(_.item === itemId).drop(offset).take(limit).result)
  }
}
