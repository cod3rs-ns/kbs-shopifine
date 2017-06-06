package hateoas

import commons.CollectionLinks
import domain.BuyerCategory

package object buyer_categories {

  case class BuyerCategoryRequestAttributes(name: String)

  case class BuyerCategoryRequestData(`type`: String, attributes: BuyerCategoryRequestAttributes)

  case class BuyerCategoryRequest(data: BuyerCategoryRequestData) {
    def toDomain: BuyerCategory = BuyerCategory(name = data.attributes.name)
  }

  case class BuyerCategoryResponseAttributes(name: String, thresholds: String)

  case class BuyerCategoryResponseData(`type`: String, id: Long, attributes: BuyerCategoryResponseAttributes)

  object BuyerCategoryResponseData {
    def fromDomain(buyerCategory: BuyerCategory): BuyerCategoryResponseData = {
      BuyerCategoryResponseData(
        `type` = BuyerCategoriesType,
        id = buyerCategory.id.get,
        attributes = BuyerCategoryResponseAttributes(
          name = buyerCategory.name,
          thresholds = s"/api/buyer-categories/${buyerCategory.id.get}/thresholds"
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
}
