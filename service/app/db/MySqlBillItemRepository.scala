package db

import javax.inject.Inject

import domain.BillItem
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.BillItemRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._


import scala.concurrent.Future

class MySqlBillItemRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends BillItemRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(billItem: BillItem): Future[BillItem] = {
    val items = billItems returning billItems.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += billItem)
  }

  override def retrieveByBill(billId: Long): Future[Seq[BillItem]] = {
    db.run(billItems.filter(_.billId === billId).result)
  }
}
