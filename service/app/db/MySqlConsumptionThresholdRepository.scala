package db

import javax.inject.Inject

import domain.ConsumptionThreshold
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.ConsumptionThresholdRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

class MySqlConsumptionThresholdRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends ConsumptionThresholdRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(threshold: ConsumptionThreshold): Future[ConsumptionThreshold] = {
    val items = consumptionThresholds returning consumptionThresholds.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += threshold)
  }

  override def retrieveByBuyerCategory(id: Long, offset: Int, limit: Int): Future[Seq[ConsumptionThreshold]] = {
    db.run(consumptionThresholds.drop(offset).take(limit).result)
  }

  override def delete(id: Long): Future[Int] = {
    db.run(consumptionThresholds.filter(_.id === id).delete)
  }

}
