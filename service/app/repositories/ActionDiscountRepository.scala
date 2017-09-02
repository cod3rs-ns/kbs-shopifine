package repositories

import domain.{ActionDiscount, ActionDiscountProductCategory}

import scala.concurrent.Future

trait ActionDiscountRepository {

  def save(actionDiscount: ActionDiscount): Future[ActionDiscount]

  def findOne(id: Long): Future[Option[ActionDiscount]]

  def retrieveAll(offset: Int, limit: Int): Future[Seq[ActionDiscount]]

  def modify(id: Long, actionDiscount: ActionDiscount): Future[Int]

  def retrieveByProductCategory(category: Long): Future[Seq[(ActionDiscount, ActionDiscountProductCategory)]]

  def addProductCategory(id: Long, categoryId: Long): Future[ActionDiscountProductCategory]

  def removeProductCategory(id: Long, categoryId: Long): Future[Int]

}
