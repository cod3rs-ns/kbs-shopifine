package repositories

import domain.Product

import scala.concurrent.Future

trait ProductRepository {

  def save(product: Product): Future[Product]

  def retrieve(id: Long): Future[Option[Product]]

  def retrieveAll(): Future[Seq[Product]]

  def fillStock(id: Long, quantity: Long): Future[Option[Int]]

  def updateLastBoughtDateTime(id: Long): Future[Int]

  def updateFillStock(id: Long): Future[Int]

}
