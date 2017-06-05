package db

import javax.inject.Inject

import domain.Product
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.ProductRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

class MySqlProductRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends ProductRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(product: Product): Future[Product] = {
    val items = products returning products.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += product)
  }

  override def retrieve(id: Long): Future[Option[Product]] = ???

  override def retrieveAll: Future[Seq[Product]] = ???

}
