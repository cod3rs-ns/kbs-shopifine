package repositories

import domain.BuyerCategory

import scala.concurrent.Future

trait BuyerCategoryRepository {

  def save(buyerCategory: BuyerCategory): Future[BuyerCategory]

  def retrieveAll(offset: Int, limit: Int): Future[Seq[BuyerCategory]]

}
