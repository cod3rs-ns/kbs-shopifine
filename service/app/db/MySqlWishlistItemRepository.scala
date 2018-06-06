package db

import domain.WishlistItem
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.WishlistItemsRepository
import slick.driver.JdbcProfile

import slick.driver.MySQLDriver.api._
import scala.concurrent.Future

class MySqlWishlistItemRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
    extends WishlistItemsRepository
    with HasDatabaseConfigProvider[JdbcProfile]
    with DatabaseSchema {

  override def save(wishlistItem: WishlistItem): Future[WishlistItem] = {
    val items = wishlistItems returning wishlistItems.map(_.id) into ((item,
                                                                       id) =>
                                                                        item.copy(id = Some(id)))
    db.run(items += wishlistItem)
  }

  override def retrieve(id: Long): Future[Option[WishlistItem]] =
    db.run(wishlistItems.filter(_.id === id).result.headOption)

  override def delete(id: Long): Future[Int] =
    db.run(wishlistItems.filter(_.id === id).delete)

  override def retrieveAll(userId: Long): Future[Seq[WishlistItem]] =
    db.run(wishlistItems.filter(_.customerId === userId).result)

  override def count(userId: Long, productId: Long): Future[Int] =
    db.run(
      wishlistItems
        .filter(_.customerId === userId)
        .filter(_.productId === productId)
        .countDistinct
        .result)
}
