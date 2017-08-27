package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.BillItem
import com.dmarjanovic.drools.external.{BillsProxy, ProductsProxy}
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class BillItemRequestAttributes(price: Double, quantity: Int, discount: Double)

case class BillItemRequestRelationships(product: RequestRelationship, bill: RequestRelationship)

case class BillItemRequestData(`type`: String, attributes: BillItemRequestAttributes, relationships: BillItemRequestRelationships)

case class BillItemRequest(data: BillItemRequestData) {
  def toDomain(userId: Long): Future[BillItem] = {
    BillsProxy.retrieveBill(userId, data.relationships.bill.data.id).flatMap(bill =>
      ProductsProxy.retrieveProduct(data.relationships.product.data.id).map(product =>
        BillItem(
          product = Some(product),
          price = data.attributes.price,
          quantity = data.attributes.quantity,
          billCreatedAt = bill.createdAt
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

case class BillItemResponseAttributes(ordinal: Int, price: Double, quantity: Int, amount: Double, discount: Double, discountAmount: Double)

case class BillItemResponseRelationships(product: ResponseRelationship, bill: ResponseRelationship, discounts: ResponseRelationshipCollection)

case class BillItemResponseData(`type`: String, id: Long, attributes: BillItemResponseAttributes, relationships: BillItemResponseRelationships)

case class BillItemCollectionResponse(data: Seq[BillItemResponseData], links: CollectionLinks) {
  def toDomain: Seq[BillItem] = {
    data.map(item =>
      BillItem(
        price = item.attributes.price,
        quantity = item.attributes.quantity,
        amount = item.attributes.amount,
        discount = item.attributes.discount,
        discountAmount = item.attributes.discountAmount,
        billCreatedAt = DateTime.now
      )
    )
  }
}
