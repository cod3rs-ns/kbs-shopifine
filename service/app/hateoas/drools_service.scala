package hateoas

import commons.CollectionLinks
import domain._
import relationships.{ResponseRelationship, ResponseRelationshipCollection}

package object drools_service {

  case class DiscountResponse(`type`: String, discount: Double) {
    def toBillDiscount(bill: Bill): BillDiscount =
      BillDiscount(
        `type` = DiscountType.valueOf(`type`.toUpperCase),
        discount = discount,
        billId = bill.id.get
      )

    def toBillItemDiscount(item: BillItem): BillItemDiscount =
      BillItemDiscount(
        `type` = DiscountType.valueOf(`type`.toUpperCase),
        discount = discount,
        itemId = item.id.get
      )
  }

  case class BillWithDiscountsResponse(amount: Double,
                                       discount: Double,
                                       discountAmount: Double,
                                       pointsSpent: Long,
                                       pointsGained: Long,
                                       discounts: Seq[DiscountResponse])

  case class BillItemWithDiscountsResponse(price: Double,
                                           amount: Double,
                                           discount: Double,
                                           discountAmount: Double,
                                           discounts: Seq[DiscountResponse])

  case class DroolsProductResponseAttributes(quantity: Long, fillStock: Boolean, minQuantity: Long)

  case class DroolsProductResponseRelationships(category: ResponseRelationship, discounts: ResponseRelationshipCollection)

  case class DroolsProductResponseData(`type`: String,
                                 id: Long,
                                 attributes: DroolsProductResponseAttributes,
                                 relationships: DroolsProductResponseRelationships)

  case class DroolsProductResponseCollection(data: Seq[DroolsProductResponseData], links: CollectionLinks)

}
