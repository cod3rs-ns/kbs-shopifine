package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.BillItem
import com.dmarjanovic.drools.external.{BillsProxy, ProductsProxy}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class BillItemRequestAttributes(price: Double, quantity: Int, discount: Double)

case class BillItemRequestRelationships(product: RequestRelationship, bill: RequestRelationship)

case class BillItemRequestData(`type`: String, attributes: BillItemRequestAttributes, relationships: BillItemRequestRelationships)

case class BillItemRequest(data: BillItemRequestData) {
  def toDomain: Future[BillItem] = {
    BillsProxy.retrieveBill(1, data.relationships.bill.data.id).flatMap(bill =>
      ProductsProxy.retrieveProduct(data.relationships.product.data.id).map(product =>
        BillItem(
          product = Some(product),
          bill = Some(bill),
          price = data.attributes.price,
          quantity = data.attributes.quantity
        )
      )
    )
  }
}

case class BillItemWithDiscountsResponse(price: Double, amount: Double, discount: Double, discountAmount: Double, discounts: Seq[DiscountResponse])

object BillItemWithDiscountsResponseJson {
  def fromDomain(item: BillItem): BillItemWithDiscountsResponse = {
    BillItemWithDiscountsResponse(
      price = item.price,
      amount = item.amount,
      discount = item.discount,
      discountAmount = item.discountAmount,
      discounts = item.discounts.map(DiscountResponseJson.fromDomain)
    )
  }
}
