package db

import javax.inject.Inject

import domain.{ActionDiscount, ActionDiscountProductCategory}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.ActionDiscountRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.{ExecutionContext, Future}

class MySqlActionDiscountRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit val ec: ExecutionContext)
  extends ActionDiscountRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(actionDiscount: ActionDiscount): Future[ActionDiscount] = {
    val items = actionDiscounts returning actionDiscounts.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += actionDiscount)
  }

  override def retrieveAll(offset: Int, limit: Int): Future[Seq[ActionDiscount]] = {
    db.run(actionDiscounts.drop(offset).take(limit).result)
  }

  override def modify(id: Long, actionDiscount: ActionDiscount): Future[Int] = {
    val q = for {ad <- actionDiscounts if ad.id === id} yield (ad.name, ad.from, ad.to, ad.discount)
    db.run(q.update((actionDiscount.name, actionDiscount.from, actionDiscount.to, actionDiscount.discount)))
  }

  override def retrieveByProductCategory(category: Long): Future[Seq[(ActionDiscount, ActionDiscountProductCategory)]] = {
    db.run((actionDiscounts join actionDiscountsProductCategories.filter(_.category === category) on (_.id === _.discount)).result)
  }
}
