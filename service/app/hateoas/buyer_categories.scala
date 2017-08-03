package hateoas

import commons.CollectionLinks
import domain.{BuyerCategory, ConsumptionThreshold}
import relationships._

package object buyer_categories {

  case class BuyerCategoryRequestAttributes(name: String)

  case class BuyerCategoryRequestData(`type`: String, attributes: BuyerCategoryRequestAttributes)

  case class BuyerCategoryRequest(data: BuyerCategoryRequestData) {
    def toDomain: BuyerCategory = BuyerCategory(name = data.attributes.name)
  }

  case class BuyerCategoryResponseAttributes(name: String)

  case class BuyerCategoryResponseRelationships(thresholds: ResponseRelationshipCollection)

  case class BuyerCategoryResponseData(`type`: String, id: Long, attributes: BuyerCategoryResponseAttributes, relationships: BuyerCategoryResponseRelationships)

  object BuyerCategoryResponseData {
    def fromDomain(buyerCategory: BuyerCategory): BuyerCategoryResponseData = {
      BuyerCategoryResponseData(
        `type` = BuyerCategoriesType,
        id = buyerCategory.id.get,
        attributes = BuyerCategoryResponseAttributes(
          name = buyerCategory.name
        ),
        relationships = BuyerCategoryResponseRelationships(
          thresholds = ResponseRelationshipCollection(
            links = RelationshipLinks(
              related = s"/api/buyer-categories/${buyerCategory.id.get}/thresholds"
            )
          )
        )
      )
    }
  }

  case class BuyerCategoryResponse(data: BuyerCategoryResponseData)

  object BuyerCategoryResponse {
    def fromDomain(buyerCategory: BuyerCategory): BuyerCategoryResponse = {
      BuyerCategoryResponse(BuyerCategoryResponseData.fromDomain(buyerCategory))
    }
  }

  case class BuyerCategoryCollectionResponse(data: Seq[BuyerCategoryResponseData], links: CollectionLinks)

  object BuyerCategoryCollectionResponse {
    def fromDomain(buyerCategories: Seq[BuyerCategory], links: CollectionLinks): BuyerCategoryCollectionResponse = {
      BuyerCategoryCollectionResponse(
        data = buyerCategories.map(BuyerCategoryResponseData.fromDomain),
        links = links
      )
    }
  }

  case class ConsumptionThresholdAttributes(from: Int, to: Int, award: Double)

  case class ConsumptionThresholdRequestRelationships(category: RequestRelationship)

  case class ConsumptionThresholdRequestData(`type`: String, attributes: ConsumptionThresholdAttributes, relationships: ConsumptionThresholdRequestRelationships)

  case class ConsumptionThresholdRequest(data: ConsumptionThresholdRequestData) {
    def toDomain: ConsumptionThreshold =
      ConsumptionThreshold(
        buyerCategory = data.relationships.category.data.id,
        from = data.attributes.from,
        to = data.attributes.to,
        award = data.attributes.award
      )
  }

  case class ConsumptionThresholdResponseRelationships(category: ResponseRelationship)

  case class ConsumptionThresholdResponseData(`type`: String, id: Long, attributes: ConsumptionThresholdAttributes, relationships: ConsumptionThresholdResponseRelationships)

  object ConsumptionThresholdResponseData {
    def fromDomain(threshold: ConsumptionThreshold): ConsumptionThresholdResponseData = {
      ConsumptionThresholdResponseData(
        `type` = ConsumptionThresholdsType,
        id = threshold.id.get,
        attributes = ConsumptionThresholdAttributes(
          from = threshold.from,
          to = threshold.to,
          award = threshold.award
        ),
        relationships = ConsumptionThresholdResponseRelationships(
          category = ResponseRelationship(
            links = RelationshipLinks(
              related = s"/api/buyer-categories/${threshold.buyerCategory}"
            ),
            data = RelationshipData(
              `type` = BuyerCategoriesType,
              id = threshold.buyerCategory
            )
          )
        )
      )
    }
  }

  case class ConsumptionThresholdResponse(data: ConsumptionThresholdResponseData)

  object ConsumptionThresholdResponse {
    def fromDomain(threshold: ConsumptionThreshold): ConsumptionThresholdResponse = {
      ConsumptionThresholdResponse(ConsumptionThresholdResponseData.fromDomain(threshold))
    }
  }

  case class ConsumptionThresholdCollectionResponse(data: Seq[ConsumptionThresholdResponseData], links: CollectionLinks)

  object ConsumptionThresholdCollectionResponse {
    def fromDomain(thresholds: Seq[ConsumptionThreshold], links: CollectionLinks): ConsumptionThresholdCollectionResponse = {
      ConsumptionThresholdCollectionResponse(
        data = thresholds.map(ConsumptionThresholdResponseData.fromDomain),
        links = links
      )
    }
  }

}
