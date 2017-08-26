package hateoas

import commons.CollectionLinks
import domain.ProductCategory
import relationships._

package object product_categories {

  case class ProductCategoryRequestAttributes(name: String, maxDiscount: Double, isConsumerGoods: Option[Boolean])

  case class ProductCategoryRequestRelationships(superCategory: RequestRelationship)

  case class ProductCategoryRequestData(`type`: String, attributes: ProductCategoryRequestAttributes, relationships: Option[ProductCategoryRequestRelationships])

  case class ProductCategoryRequest(data: ProductCategoryRequestData) {
    def toDomain: ProductCategory = {
      val attributes = data.attributes

      ProductCategory(
        name = attributes.name,
        maxDiscount = attributes.maxDiscount,
        isConsumerGoods = if (attributes.isConsumerGoods.isDefined) attributes.isConsumerGoods.get else false,
        superCategoryId = if (data.relationships.isDefined) Some(data.relationships.get.superCategory.data.id) else None
      )
    }
  }

  case class ProductCategoryResponseAttributes(name: String, maxDiscount: Double, isConsumerGoods: Boolean)

  case class ProductCategoryResponseRelationships(superCategory: Option[ResponseRelationship], subcategories: ResponseRelationshipCollection)

  case class ProductCategoryResponseData(id: Long, `type`: String, attributes: ProductCategoryResponseAttributes, relationships: ProductCategoryResponseRelationships)

  object ProductCategoryResponseData {
    def fromDomain(category: ProductCategory): ProductCategoryResponseData = {
      val superCategoryRelationship =
        if (category.superCategoryId.isDefined)
          Some(ResponseRelationship(
            data = RelationshipData(
              `type` = ProductCategories,
              id = category.superCategoryId.get
            ),
            links = RelationshipLinks(
              related = s"/api/product-categories/${category.superCategoryId.get}"
            )
          ))
        else None

      val relationships =
        ProductCategoryResponseRelationships(
          superCategory = superCategoryRelationship,
          subcategories = ResponseRelationshipCollection(
            links = RelationshipLinks(
              related = s"/api/product-categories/${category.id.get}/subcategories"
            )
          )
        )

      ProductCategoryResponseData(
        id = category.id.get,
        `type` = ProductCategories,
        attributes = ProductCategoryResponseAttributes(
          name = category.name,
          maxDiscount = category.maxDiscount,
          isConsumerGoods = category.isConsumerGoods
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
