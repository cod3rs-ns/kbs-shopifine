package repositories

import domain.BuyerCategory

import scala.concurrent.Future

trait BuyerCategoryRepository {

  def save(buyerCategory: BuyerCategory): Future[BuyerCategory]

  def retrieveOne(id: Long): Future[Option[BuyerCategory]]

  def retrieveAll(offset: Int, limit: Int): Future[Seq[BuyerCategory]]

  def modify(id: Long, category: BuyerCategory): Future[Int]

}
