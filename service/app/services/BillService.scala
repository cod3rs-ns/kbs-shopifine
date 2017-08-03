package services

import com.google.inject.Inject
import domain.{Bill, BillState}
import repositories.BillRepository

import scala.concurrent.{ExecutionContext, Future}

class BillService @Inject()(repository: BillRepository)
                           (implicit ec: ExecutionContext) {

  import BillService.Status

  def save(bill: Bill): Future[Bill] =
    repository.save(bill)

  def retrieveOne(id: Long): Future[Option[Bill]] =
    repository.retrieveOne(id)

  def retrieveBillsWithFilters(filters: Map[String, String], offset: Int, limit: Int): Future[Seq[Bill]] = {
    repository.retrieveAll(offset, limit).map(bills =>
      bills.filter(bill => {
        filters.keys.forall(name => {
          val value = filters(name)
          name match {
            case Status => bill.state.toString.toUpperCase == value.toUpperCase
            case _ => true
          }
        })
      }).slice(offset, offset + limit)
    )
  }

  def retrieveByUser(userId: Long, offset: Int, limit: Int): Future[Seq[Bill]] =
    repository.retrieveByUser(userId, offset, limit)

  def setState(id: Long, state: BillState): Future[Int] =
    repository.setState(id, state)

}

object BillService {
  val Status: String = "status"
}
