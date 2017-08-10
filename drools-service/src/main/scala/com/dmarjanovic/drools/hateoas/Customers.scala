package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.{User, UserRole}
import org.joda.time.DateTime

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
  def toDomain: User = {
    User(
      username = data.attributes.username,
      firstName = data.attributes.firstName,
      lastName = data.attributes.lastName,
      role = UserRole.valueOf(data.attributes.role.toUpperCase),
      address = Some(data.attributes.address),
      registeredAt = DateTime.parse(data.meta.registeredAt)
    )
  }
}
