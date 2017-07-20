package hateoas

import commons.CollectionLinks
import domain.ProductCategory
import relationships.{RelationshipData, RelationshipLinks, RequestRelationship, ResponseRelationship}

package object product_categories {

  case class ProductCategoryAttributes(name: String, maxDiscount: Double)

  case class ProductCategoryRequestRelationships(superCategory: RequestRelationship)

  case class ProductCategoryRequestData(`type`: String, attributes: ProductCategoryAttributes, relationships: Option[ProductCategoryRequestRelationships])

  case class ProductCategoryRequest(data: ProductCategoryRequestData) {
    def toDomain: ProductCategory = {
      val attributes = data.attributes

      ProductCategory(
        name = attributes.name,
        maxDiscount = attributes.maxDiscount,
        superCategoryId = if (data.relationships.isDefined) Some(data.relationships.get.superCategory.data.id) else None
      )
    }
  }

  case class ProductCategoryResponseRelationships(superCategory: ResponseRelationship)

  case class ProductCategoryResponseData(id: Long, `type`: String, attributes: ProductCategoryAttributes, relationships: Option[ProductCategoryResponseRelationships])

  object ProductCategoryResponseData {
    def fromDomain(category: ProductCategory): ProductCategoryResponseData = {
      val relationships =
        if (category.superCategoryId.isDefined)
          Some(ProductCategoryResponseRelationships(
            superCategory = ResponseRelationship(
              data = RelationshipData(
                `type` = ProductCategories,
                id = category.superCategoryId.get
              ),
              links = RelationshipLinks(
                related = s"api/product-categories/${category.superCategoryId.get}"
              )
            )
          ))
        else
          None

      ProductCategoryResponseData(
        id = category.id.get,
        `type` = ProductCategories,
        attributes = ProductCategoryAttributes(
          name = category.name,
          maxDiscount = category.maxDiscount
        ),
        relationships = relationships
      )
    }
  }

  case class ProductCategoryResponse(data: ProductCategoryResponseData)

  object ProductCategoryResponse {
    def fromDomain(category: ProductCategory): ProductCategoryResponse = ProductCategoryResponse(data = ProductCategoryResponseData.fromDomain(category))
  }

  case class ProductCategoryCollectionResponse(data: Seq[ProductCategoryResponseData], links: CollectionLinks)

  object ProductCategoryCollectionResponse {
    def fromDomain(categories: Seq[ProductCategory], links: CollectionLinks): ProductCategoryCollectionResponse = {
      ProductCategoryCollectionResponse(categories.map(ProductCategoryResponseData.fromDomain), links)
    }
  }

}
