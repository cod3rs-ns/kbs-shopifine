package repositories

import domain.Product
import scala.concurrent.Future

trait ProductRepository {

  def save(product: Product): Future[Product]

  def retrieve(id: Long): Future[Option[Product]]

  def retrieveAll: Future[Seq[Product]]

}
