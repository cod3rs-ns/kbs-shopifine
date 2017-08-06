package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.{Bill, BillState}
import org.joda.time.DateTime


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
  def toDomain: Bill = {
    Bill(
      id = Some(data.id),
      createdAt = DateTime.parse(data.attributes.createdAt),
      customer = None,
      state = BillState.valueOf(data.attributes.state.toUpperCase),
      amount = data.attributes.amount,
      discount = data.attributes.discount,
      discountAmount = data.attributes.discountAmount,
      pointsSpent = data.attributes.pointsSpent,
      pointsGained = data.attributes.pointsGained
    )
  }
}