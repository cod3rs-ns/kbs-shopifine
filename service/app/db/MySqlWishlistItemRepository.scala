package db

import domain.{User, WishlistItem}
import javax.inject.Inject

import akka.NotUsed
import akka.stream.scaladsl.Source
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

  override def findUsersWithProductInWishlist(productId: Long): Source[Long, NotUsed] = {
    val action =
      wishlistItems
        .join(users)
        .on(_.customerId === _.id)
        .filter(_._1.productId === productId)
        .map(_._2.id)
        .distinct
        .result

    val databasePublisher = db stream action
    Source fromPublisher databasePublisher
  }

  override def deleteProduct(productId: Long): Future[Int] =
    db.run(wishlistItems.filter(_.productId === productId).delete)

}
