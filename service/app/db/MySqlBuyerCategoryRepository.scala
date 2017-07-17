package db

import javax.inject.Inject

import domain.BuyerCategory
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.BuyerCategoryRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

class MySqlBuyerCategoryRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends BuyerCategoryRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(buyerCategory: BuyerCategory): Future[BuyerCategory] = {
    val items = buyerCategories returning buyerCategories.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += buyerCategory)
  }

  override def retrieveOne(id: Long): Future[Option[BuyerCategory]] = {
    db.run(buyerCategories.filter(_.id === id).result.headOption)
  }

  override def retrieveAll(offset: Int, limit: Int): Future[Seq[BuyerCategory]] = {
    db.run(buyerCategories.drop(offset).take(limit).result)
  }

}
