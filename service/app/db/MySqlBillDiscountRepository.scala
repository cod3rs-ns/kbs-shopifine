package db

import javax.inject.Inject

import domain.BillDiscount
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.BillDiscountRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

class MySqlBillDiscountRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends BillDiscountRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(discount: BillDiscount): Future[BillDiscount] = {
    val items = billDiscounts returning billDiscounts.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += discount)
  }

  override def retrieveOne(id: Long): Future[Option[BillDiscount]] = {
    db.run(billDiscounts.filter(_.id === id).result.headOption)
  }

  override def retrieveAll(billId: Long, offset: Int, limit: Int): Future[Seq[BillDiscount]] = {
    db.run(billDiscounts.filter(_.billId === billId).drop(offset).take(limit).result)
  }

}
