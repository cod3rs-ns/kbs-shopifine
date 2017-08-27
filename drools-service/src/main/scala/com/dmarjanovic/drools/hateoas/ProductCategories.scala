package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.ProductCategory
import com.dmarjanovic.drools.external.{ActionDiscountsProxy, ProductsProxy}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


case class ProductCategoryResponseAttributes(name: String, maxDiscount: Double, isConsumerGoods: Boolean)

case class ProductCategoryResponseRelationships(superCategory: Option[ResponseRelationship], subcategories: ResponseRelationshipCollection)

case class ProductCategoryResponseData(id: Long, `type`: String, attributes: ProductCategoryResponseAttributes, relationships: ProductCategoryResponseRelationships)

case class ProductCategoryResponse(data: ProductCategoryResponseData) {
  def toDomain: Future[ProductCategory] = {
    ProductsProxy.retrieveProducts(Some(data.id)).flatMap(products => {
      ActionDiscountsProxy.retrieveActionDiscountsFor(data.id).map(discounts => {
        ProductCategory(
          name = data.attributes.name,
          maxDiscount = data.attributes.maxDiscount,
          isConsumerGoods = data.attributes.isConsumerGoods,
          products = products,
          actionDiscounts = discounts
        )
      })
    })
  }
}
