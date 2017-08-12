package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.ProductCategory


case class ProductCategoryResponseAttributes(name: String, maxDiscount: Double, isConsumerGoods: Boolean)

case class ProductCategoryResponseRelationships(superCategory: Option[ResponseRelationship], subcategories: ResponseRelationshipCollection)

case class ProductCategoryResponseData(id: Long, `type`: String, attributes: ProductCategoryResponseAttributes, relationships: ProductCategoryResponseRelationships)

case class ProductCategoryResponse(data: ProductCategoryResponseData) {
  def toDomain: ProductCategory = {
    ProductCategory(
      name = data.attributes.name,
      maxDiscount = data.attributes.maxDiscount,
      isConsumerGoods = data.attributes.isConsumerGoods
    )
  }
}
