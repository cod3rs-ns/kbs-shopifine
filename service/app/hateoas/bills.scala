import commons.CollectionLinks
import domain.{Bill, BillItem, BillState}
import org.joda.time.DateTime
import relationships._

package object bills {

  import hateoas._

  case class BillRequestAttributes(state: String)

  case class BillRequestRelationships(customer: RequestRelationship)

  case class BillRequestData(`type`: String, attributes: BillRequestAttributes, relationships: BillRequestRelationships)

  case class BillRequest(data: BillRequestData) {

    def toDomain: Bill = {
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

  case class BillResponseAttributes(createdAt: DateTime,
                                    state: String,
                                    amount: Double,
                                    discount: Double,
                                    discountAmount: Double,
                                    pointsGained: Long,
                                    pointsSpent: Long)

  case class BillResponseRelationships(customer: ResponseRelationship, items: ResponseRelationshipCollection)

  case class BillResponseData(`type`: String, attributes: BillResponseAttributes, relationships: BillResponseRelationships)

  object BillResponseData {

    def fromDomain(bill: Bill): BillResponseData = {

      val attributes = BillResponseAttributes(
        createdAt = bill.createdAt,
        state = bill.state.toString,
        amount = bill.amount,
        discount = bill.discount,
        discountAmount = bill.discountAmount,
        pointsGained = bill.pointsGained,
        pointsSpent = bill.pointsSpent
      )

      val relationships = BillResponseRelationships(
        customer = ResponseRelationship(
          links = RelationshipLinks("self", "related"),
          data = RelationshipData(
            `type` = UsersType,
            id = bill.customerId
          )),
        items = ResponseRelationshipCollection(
          links = RelationshipLinks("self", "related")
        )
      )

      BillResponseData(
        `type` = BillsType,
        attributes = attributes,
        relationships = relationships
      )
    }

  }

  case class BillResponse(data: BillResponseData)

  object BillResponse {
    def fromDomain(bill: Bill): BillResponse = BillResponse(data = BillResponseData.fromDomain(bill))
  }

  case class BillCollectionResponse(data: Seq[BillResponseData], links: CollectionLinks)

  object BillCollectionResponse {
    def fromDomain(bills: Seq[Bill], links: CollectionLinks): BillCollectionResponse = {
      BillCollectionResponse(
        data = bills.map(BillResponseData.fromDomain),
        links = links
      )
    }
  }

}
