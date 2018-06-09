package repositories

import akka.NotUsed
import akka.stream.scaladsl.Source
import domain.{User, WishlistItem}

import scala.concurrent.Future

trait WishlistItemsRepository {
  def save(wishlist: WishlistItem): Future[WishlistItem]

  def retrieve(id: Long): Future[Option[WishlistItem]]

  def retrieveAll(userId: Long): Future[Seq[WishlistItem]]

  def delete(id: Long): Future[Int]

  def deleteProduct(productId: Long): Future[Int]

  def count(userId: Long, productId: Long): Future[Int]

  def findUsersWithProductInWishlist(productId: Long): Source[Long, NotUsed]
}
