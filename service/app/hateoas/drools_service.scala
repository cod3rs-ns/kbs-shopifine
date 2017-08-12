package hateoas

import domain._

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

}
