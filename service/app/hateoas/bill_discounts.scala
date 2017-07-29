package hateoas

import commons.CollectionLinks
import domain.BillDiscount
import relationships.{RelationshipData, RelationshipLinks, ResponseRelationship}

package object bill_discounts {

  case class BillDiscountAttributes(discount: Double, `type`: String)

  case class BillDiscountRelationships(bill: ResponseRelationship)

  case class BillDiscountData(`type`: String, id: Long, attributes: BillDiscountAttributes, relationships: BillDiscountRelationships)

  object BillDiscountData {
    def fromDomain(discount: BillDiscount, userId: Long): BillDiscountData = {
      BillDiscountData(
        `type` = BillDiscountsType,
        id = discount.id.get,
        attributes = BillDiscountAttributes(
          discount = discount.discount,
          `type` = discount.`type`.toString
        ),
        relationships = BillDiscountRelationships(
          bill = ResponseRelationship(
            links = RelationshipLinks(
              related = s"/api/users/$userId/bills/${discount.billId}"
            ),
            data = RelationshipData(
              `type` = BillsType,
              id = discount.billId
            )
          )
        )
      )
    }
  }

  case class BillDiscountResponse(data: BillDiscountData)

  object BillDiscountResponse {
    def fromDomain(discount: BillDiscount, userId: Long): BillDiscountResponse = {
      BillDiscountResponse(
        data = BillDiscountData.fromDomain(discount, userId)
      )
    }
  }

  case class BillDiscountCollectionResponse(data: Seq[BillDiscountData], links: CollectionLinks)

  object BillDiscountCollectionResponse {
    def fromDomain(discounts: Seq[BillDiscount], links: CollectionLinks, userId: Long): BillDiscountCollectionResponse = {
      BillDiscountCollectionResponse(
        data = discounts.map(BillDiscountData.fromDomain(_, userId)),
        links = links
      )
    }
  }

}
