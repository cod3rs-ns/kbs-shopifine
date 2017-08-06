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
          // FIXME Determine Ordinal
          ordinal = 1,
          product = Some(product),
          bill = Some(bill),
          price = data.attributes.price,
          quantity = data.attributes.quantity,
          amount = 0,
          discount = 0,
          discountAmount = 0
        )
      )
    )
  }
}
