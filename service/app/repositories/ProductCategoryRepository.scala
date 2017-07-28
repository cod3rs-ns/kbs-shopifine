package repositories

import domain.ProductCategory

import scala.concurrent.Future

trait ProductCategoryRepository {

  def save(category: ProductCategory): Future[ProductCategory]

  def retrieveAll(offset: Int, limit: Int): Future[Seq[ProductCategory]]

  def retrieveAllSubcategories(id: Long, offset: Int, limit: Int): Future[Seq[ProductCategory]]

  def modify(id: Long, category: ProductCategory): Future[Int]

  def findOne(id: Long): Future[Option[ProductCategory]]

}
