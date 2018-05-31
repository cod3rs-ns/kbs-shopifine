package repositories

import domain.WishlistItem

import scala.concurrent.Future

trait WishlistItemsRepository {
  def save(wishlist: WishlistItem): Future[WishlistItem]

  def retrieve(id: Long): Future[Option[WishlistItem]]

  def retrieveAll(): Future[Seq[WishlistItem]]

  def delete(id: Long): Future[Int]
}
