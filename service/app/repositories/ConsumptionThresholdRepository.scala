package repositories

import domain.ConsumptionThreshold

import scala.concurrent.Future

trait ConsumptionThresholdRepository {

  def save(threshold: ConsumptionThreshold): Future[ConsumptionThreshold]

  def retrieveByBuyerCategory(id: Long, offset: Int = 0, limit: Int = Int.MaxValue): Future[Seq[ConsumptionThreshold]]

  def delete(id: Long): Future[Int]

}
