package hateoas

import commons.CollectionLinks
import domain.BillItemDiscount
import relationships.{RelationshipData, RelationshipLinks, ResponseRelationship}

package object bill_item_discounts {

  case class BillItemDiscountAttributes(discount: Double, `type`: String)

  case class BillItemDiscountRelationships(billItem: ResponseRelationship)

  case class BillItemDiscountData(`type`: String, id: Long, attributes: BillItemDiscountAttributes, relationships: BillItemDiscountRelationships)

  object BillItemDiscountData {
    def fromDomain(discount: BillItemDiscount, userId: Long, billId: Long): BillItemDiscountData = {
      BillItemDiscountData(
        `type` = BillItemDiscountsType,
        id = discount.id.get,
        attributes = BillItemDiscountAttributes(
          discount = discount.discount,
          `type` = discount.`type`.toString
        ),
        relationships = BillItemDiscountRelationships(
          billItem = ResponseRelationship(
            links = RelationshipLinks(
              related = s"/api/users/$userId/bills/$billId/bill-items/${discount.id.get}"
            ),
            data = RelationshipData(
              `type` = BillItemsType,
              id = discount.itemId
            )
          )
        )
      )
    }
  }

  case class BillItemDiscountResponse(data: BillItemDiscountData)

  object BillItemDiscountResponse {
    def fromDomain(discount: BillItemDiscount, userId: Long, billId: Long): BillItemDiscountResponse = {
      BillItemDiscountResponse(
        data = BillItemDiscountData.fromDomain(discount, userId, billId)
      )
    }
  }

  case class BillItemDiscountCollectionResponse(data: Seq[BillItemDiscountData], links: CollectionLinks)

  object BillItemDiscountCollectionResponse {
    def fromDomain(discounts: Seq[BillItemDiscount], links: CollectionLinks, userId: Long, billId: Long): BillItemDiscountCollectionResponse = {
      BillItemDiscountCollectionResponse(
        data = discounts.map(BillItemDiscountData.fromDomain(_, userId, billId)),
        links = links
      )
    }
  }

}
