package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.User
import com.dmarjanovic.drools.external.BuyerCategoriesProxy
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CustomerResponseAttributes(username: String,
                                      firstName: String,
                                      lastName: String,
                                      role: String,
                                      address: String,
                                      points: Long)

case class CustomerResponseRelationships(buyerCategory: ResponseRelationship)

case class CustomerResponseMeta(registeredAt: String)

case class CustomerResponseData(`type`: String,
                                id: Long,
                                attributes: CustomerResponseAttributes,
                                meta: CustomerResponseMeta,
                                relationships: CustomerResponseRelationships)

case class CustomerResponse(data: CustomerResponseData) {
  def toDomain: Future[User] = {
    BuyerCategoriesProxy.retrieveBuyerCategory(data.relationships.buyerCategory.data.id).map(category =>
      User(
        buyerCategory = Some(category),
        registeredAt = DateTime.parse(data.meta.registeredAt)
      )
    )
  }
}
