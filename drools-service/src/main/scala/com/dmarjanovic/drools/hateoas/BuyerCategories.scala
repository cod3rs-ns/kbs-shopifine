package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.BuyerCategory
import com.dmarjanovic.drools.external.BuyerCategoriesProxy

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class BuyerCategoryResponseAttributes(name: String)

case class BuyerCategoryResponseRelationships(thresholds: ResponseRelationshipCollection)

case class BuyerCategoryResponseData(`type`: String, id: Long, attributes: BuyerCategoryResponseAttributes, relationships: BuyerCategoryResponseRelationships)

case class BuyerCategoryResponse(data: BuyerCategoryResponseData) {
  def toDomain: Future[BuyerCategory] =
    BuyerCategoriesProxy.retrieveThresholdsForBuyerCategory(data.id).map(thresholds =>
      BuyerCategory(
        id = Some(data.id),
        name = data.attributes.name,
        thresholds = thresholds
      )
    )
}