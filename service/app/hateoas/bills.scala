import domain.{Bill, BillState}
import org.joda.time.DateTime
import relationships.RequestRelationship

package object bills {

  case class BillRequestAttributes(state: String)

  case class BillRequestRelationships(customer: RequestRelationship, items: Seq[RequestRelationship])

  case class BillRequestData(`type`: String, attributes: BillRequestAttributes, relationships: BillRequestRelationships)

  case class BillRequest(data: BillRequestData) {

    def toDomain(): Bill = {
      val attributes = data.attributes
      val relationships = data.relationships

      Bill(
        createdAt = DateTime.now,
        customerId = relationships.customer.data.id,
        state = BillState.valueOf(attributes.state.toUpperCase),
        amount = 0,
        discount = 0,
        discountAmount = 0,
        pointsGained = 0,
        pointsSpent = 0
      )
    }
  }

}
