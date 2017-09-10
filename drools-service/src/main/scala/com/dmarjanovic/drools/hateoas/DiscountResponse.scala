package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.{BillDiscount, BillItemDiscount}

case class DiscountResponse(name: String, `type`: String, discount: Double)

object DiscountResponseJson {
  def fromDomain(discount: BillDiscount): DiscountResponse =
    DiscountResponse(
      name = discount.name,
      `type` = discount.`type`.toString,
      discount = discount.discount
    )

  def fromDomain(discount: BillItemDiscount): DiscountResponse =
    DiscountResponse(
      name = discount.name,
      `type` = discount.`type`.toString,
      discount = discount.discount
    )
}
