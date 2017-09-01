package services

import com.google.inject.Inject
import domain.{Bill, BillItem, BillState}
import repositories.{BillItemRepository, BillRepository, ProductRepository, UserRepository}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class BillService @Inject()(repository: BillRepository,
                            billItems: BillItemRepository,
                            products: ProductRepository,
                            users: UserRepository)
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

  def setState(id: Long, state: BillState): Future[Int] = {
    if (BillState.SUCCESSFUL == state) {
      retrieveOne(id).flatMap(bill =>
        setBillToSuccessful(bill.get)
      )
    } else {
      repository.setState(id, state)
    }
  }

  def enlargeAmount(id: Long, amount: Double): Future[Int] =
    repository.enlargeAmount(id, amount)

  def updateBillCalculation(bill: Bill): Future[Int] =
    repository.modify(bill.id.get, bill)

  private def setBillToSuccessful(bill: Bill): Future[Int] = {
    val billId = bill.id.get

    billItems.retrieveByBill(billId, 0, Int.MaxValue).flatMap(items =>
      if (hasBillItems(items)) {
        // Update products quantity
        items.foreach(item => products.fillStock(item.productId, -item.quantity))

        users.retrieve(bill.customerId).map(_.get).flatMap(user => {
          // Update User Points
          if (user.points.get > bill.pointsSpent) {
            // Update User points
            users.updateUserPoints(bill.customerId, bill.pointsGained - bill.pointsSpent)

            // Set Bill State
            repository.setState(billId, BillState.SUCCESSFUL)
          } else {
            Future.successful(-2)
          }
        })
      } else {
        Future.successful(-1)
      }
    )
  }

  private def hasBillItems(items: Seq[BillItem]): Boolean = {
    items.forall(i => {
      Await.result(products.retrieve(i.productId).map(_.get).map(p => {
        p.quantity >= i.quantity
      }), 3.seconds)
    })
  }

}

object BillService {
  val Status: String = "status"
}
