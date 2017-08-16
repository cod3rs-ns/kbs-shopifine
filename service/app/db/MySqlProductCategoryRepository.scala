package db

import javax.inject.Inject

import domain.{ActionDiscountProductCategory, ProductCategory}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.ProductCategoryRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

class MySqlProductCategoryRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends ProductCategoryRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(category: ProductCategory): Future[ProductCategory] = {
    val items = productCategories returning productCategories.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += category)
  }

  override def retrieveAll(offset: Int, limit: Int): Future[Seq[ProductCategory]] = {
    db.run(productCategories.drop(offset).take(limit).result)
  }

  override def retrieveAllSubcategories(id: Long, offset: Int, limit: Int): Future[Seq[ProductCategory]] = {
    db.run(productCategories.filter(_.superCategory === id).drop(offset).take(limit).result)
  }

  override def retrieveAllByActionDiscount(id: Long, offset: Int, limit: Int): Future[Seq[(ProductCategory, ActionDiscountProductCategory)]] = {
    db.run((productCategories join actionDiscountsProductCategories.filter(_.discount === id) on (_.id === _.category)).result)
  }

  override def modify(id: Long, category: ProductCategory): Future[Int] = {
    val q = for {c <- productCategories if c.id === id} yield (c.name, c.superCategory, c.maxDiscount)
    db.run(q.update((category.name, category.superCategoryId, category.maxDiscount)))
  }

  override def findOne(id: Long): Future[Option[ProductCategory]] = {
    db.run(productCategories.filter(_.id === id).result.headOption)
  }

}
