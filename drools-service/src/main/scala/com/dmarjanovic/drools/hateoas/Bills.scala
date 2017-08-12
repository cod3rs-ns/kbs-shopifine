package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.Bill
import com.dmarjanovic.drools.external.CustomersProxy
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class BillResponseAttributes(createdAt: String,
                                  state: String,
                                  amount: Double,
                                  discount: Double,
                                  discountAmount: Double,
                                  pointsGained: Long,
                                  pointsSpent: Long)

case class BillResponseRelationships(customer: ResponseRelationship, items: ResponseRelationshipCollection, discounts: ResponseRelationshipCollection)

case class BillResponseData(`type`: String, id: Long, attributes: BillResponseAttributes, relationships: BillResponseRelationships)

case class BillResponse(data: BillResponseData) {
  def toDomain: Future[Bill] = {
    CustomersProxy.retrieveUser(data.relationships.customer.data.id).map(customer =>
      Bill(
        id = Some(data.id),
        createdAt = DateTime.parse(data.attributes.createdAt),
        customer = Some(customer),
        amount = data.attributes.amount,
        discount = data.attributes.discount,
        discountAmount = data.attributes.discountAmount,
        pointsSpent = data.attributes.pointsSpent,
        pointsGained = data.attributes.pointsGained
      )
    )
  }
}

case class BillWithDiscountsResponse(amount: Double,
                                     discount: Double,
                                     discountAmount: Double,
                                     pointsSpent: Long,
                                     pointsGained: Long,
                                     discounts: Seq[DiscountResponse])

object BillWithDiscountsResponseJson {
  def fromDomain(bill: Bill): BillWithDiscountsResponse = {
    BillWithDiscountsResponse(
      amount = bill.amount,
      discount = bill.discount,
      discountAmount = bill.discountAmount,
      pointsSpent = bill.pointsSpent,
      pointsGained = bill.pointsGained,
      discounts = bill.discounts.map(DiscountResponseJson.fromDomain)
    )
  }
}
