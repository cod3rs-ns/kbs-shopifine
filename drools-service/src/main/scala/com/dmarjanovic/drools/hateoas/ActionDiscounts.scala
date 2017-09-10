package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.ActionDiscount
import org.joda.time.DateTime

case class ActionDiscountAttributes(name: String, from: String, to: String, discount: Double)

case class ActionDiscountResponseRelationships(categories: ResponseRelationshipCollection)

case class ActionDiscountResponseData(`type`: String, id: Long, attributes: ActionDiscountAttributes, relationships: ActionDiscountResponseRelationships)

case class ActionDiscountCollectionResponse(data: Seq[ActionDiscountResponseData], links: CollectionLinks) {
  def toDomain: Seq[ActionDiscount] = {
    data.map(discount =>
      ActionDiscount(
        name = discount.attributes.name,
        from = DateTime.parse(discount.attributes.from),
        to = DateTime.parse(discount.attributes.to),
        discount = discount.attributes.discount
      )
    )
  }
}
