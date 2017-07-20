package repositories

import domain.ActionDiscount

import scala.concurrent.Future

trait ActionDiscountRepository {

  def save(actionDiscount: ActionDiscount): Future[ActionDiscount]

  def retrieveAll(offset: Int, limit: Int): Future[Seq[ActionDiscount]]

  def modify(id: Long, actionDiscount: ActionDiscount): Future[Int]

}
