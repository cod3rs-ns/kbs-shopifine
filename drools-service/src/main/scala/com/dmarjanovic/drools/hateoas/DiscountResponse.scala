package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.{BillDiscount, BillItemDiscount}

case class DiscountResponse(`type`: String, discount: Double)

object DiscountResponseJson {
  def fromDomain(discount: BillDiscount): DiscountResponse =
    DiscountResponse(
      `type` = discount.`type`.toString,
      discount = discount.discount
    )

  def fromDomain(discount: BillItemDiscount): DiscountResponse =
    DiscountResponse(
      `type` = discount.`type`.toString,
      discount = discount.discount
    )
}
