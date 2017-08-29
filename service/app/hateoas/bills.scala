import commons.CollectionLinks
import domain.{Bill, BillState}
import org.joda.time.DateTime
import relationships._

package object bills {

  import hateoas._

  case class BillRequestAttributes(state: String, totalItems: Long, pointsSpent: Long)

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
        totalItems = attributes.totalItems,
        amount = 0,
        discount = 0,
        discountAmount = 0,
        pointsGained = 0,
        pointsSpent = attributes.pointsSpent
      )
    }
  }

  case class BillResponseAttributes(createdAt: String,
                                    state: String,
                                    totalItems: Long,
                                    amount: Double,
                                    discount: Double,
                                    discountAmount: Double,
                                    pointsGained: Long,
                                    pointsSpent: Long)

  case class BillResponseRelationships(customer: ResponseRelationship, items: ResponseRelationshipCollection, discounts: ResponseRelationshipCollection)

  case class BillResponseData(`type`: String, id: Long, attributes: BillResponseAttributes, relationships: BillResponseRelationships)

  object BillResponseData {
    def fromDomain(bill: Bill): BillResponseData = {
      val attributes = BillResponseAttributes(
        createdAt = bill.createdAt.toString,
        state = bill.state.toString,
        totalItems = bill.totalItems,
        amount = bill.amount,
        discount = bill.discount,
        discountAmount = bill.discountAmount,
        pointsGained = bill.pointsGained,
        pointsSpent = bill.pointsSpent
      )

      val relationships = BillResponseRelationships(
        customer = ResponseRelationship(
          links = RelationshipLinks(
            related = s"/api/users/${bill.customerId}"
          ),
          data = RelationshipData(
            `type` = UsersType,
            id = bill.customerId
          )),
        items = ResponseRelationshipCollection(
          links = RelationshipLinks(
            related = s"/api/users/${bill.customerId}/bills/${bill.id.get}/bill-items"
          )
        ),
        discounts = ResponseRelationshipCollection(
          links = RelationshipLinks(
            related = s"/api/users/${bill.customerId}/bills/${bill.id.get}/discounts"
          )
        )
      )

      BillResponseData(
        `type` = BillsType,
        id = bill.id.get,
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
