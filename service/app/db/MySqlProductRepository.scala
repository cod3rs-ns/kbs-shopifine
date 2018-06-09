package db

import javax.inject.Inject

import domain.Product
import org.joda.time.DateTime
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.ProductRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.{ExecutionContext, Future}

class MySqlProductRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext)
    extends ProductRepository
    with HasDatabaseConfigProvider[JdbcProfile]
    with DatabaseSchema {

  override def save(product: Product): Future[Product] = {
    val items = products returning products.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += product)
  }

  override def retrieve(id: Long): Future[Option[Product]] = {
    db.run(products.filter(_.id === id).result.headOption)
  }

  override def retrieveAll(): Future[Seq[Product]] = {
    db.run(products.result)
  }

  override def fillStock(id: Long, quantity: Long): Future[Option[Int]] = {
    val updateAction =
      sql"""
           UPDATE
              products,
              (SELECT COUNT(*) AS result FROM products WHERE id = $id AND quantity + $quantity > min_quantity) stock
           SET
              quantity = @quantity := quantity + $quantity,
              fill_stock = !stock.result
           WHERE id = $id;
        """.as[Int].head

    val selectAction = sql"""SELECT @quantity""".as[Int].head

    val composedAction = for {
      updateRes <- updateAction
      selectRes <- selectAction
    } yield if (updateRes == 1) Some(selectRes) else None

    db.run(composedAction.transactionally)
  }

  override def updateLastBoughtDateTime(id: Long): Future[Int] = {
    val q = for { product <- products if product.id === id } yield product.lastBoughtAt
    db.run(q.update(DateTime.now))
  }

  override def updateFillStock(id: Long): Future[Int] = {
    val q = for { product <- products if product.id === id } yield product.fillStock
    db.run(q.update(true))
  }

}
