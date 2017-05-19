import commons.CollectionLinks
import domain.{Product, ProductCategory}
import org.joda.time.DateTime
import relationships.{RelationshipData, RelationshipLinks, RequestRelationship, ResponseRelationship}

package object products {

  import hateoas._

  case class ProductRequestAttributes(name: String,
                                      price: Double,
                                      quantity: Long,
                                      createdAt: DateTime,
                                      fillStock: Option[Boolean],
                                      status: Option[String],
                                      minQuantity: Long)

  case class ProductRequestRelationships(category: RequestRelationship)

  case class ProductRequestData(`type`: String,
                                attributes: ProductRequestAttributes,
                                relationships: ProductRequestRelationships)

  case class ProductRequest(data: ProductRequestData) {

    def toDomain: Product = {
      val attributes = data.attributes
      val categoryRel = data.relationships.category.data

      // TODO Get Product Category
      val category = ProductCategory(
        id = Some(categoryRel.id),
        name = "test",
        maximumDiscount = 42.0
      )

      Product(
        name = attributes.name,
        category = category,
        price = attributes.price,
        quantity = attributes.quantity,
        createdAt = attributes.createdAt,
        minQuantity = attributes.minQuantity
      )
    }
  }

  case class ProductResponseAttributes(name: String,
                                       price: Double,
                                       quantity: Long,
                                       createdAt: DateTime,
                                       fillStock: Boolean,
                                       status: String,
                                       minQuantity: Long)

  case class ProductResponseRelationships(category: ResponseRelationship)

  case class ProductResponseData(`type`: String,
                                 id: Long,
                                 attributes: ProductResponseAttributes,
                                 relationships: ProductResponseRelationships)

  object ProductResponseData {

    def fromDomain(product: Product): ProductResponseData = {

      val attributes = ProductResponseAttributes(
        name = product.name,
        price = product.price,
        quantity = product.quantity,
        createdAt = product.createdAt,
        fillStock = product.fillStock,
        status = product.status.name,
        minQuantity = product.minQuantity
      )

      val relationships = ProductResponseRelationships(
        category = ResponseRelationship(
          links = RelationshipLinks("self", "related"),
          data = RelationshipData(ProductCategories, product.category.id.get)
        )
      )

      ProductResponseData(
        `type` = ProductsType,
        id = product.id.get,
        attributes = attributes,
        relationships = relationships
      )
    }
  }

  case class ProductResponse(data: ProductResponseData)

  object ProductResponse {
    def fromDomain(product: Product): ProductResponse = ProductResponse(data = ProductResponseData.fromDomain(product))
  }

  case class ProductCollectionResponse(data: Seq[ProductResponseData], links: CollectionLinks)

  object ProductCollectionResponse {
    def fromDomain(products: Seq[Product]): ProductCollectionResponse = {
      ProductCollectionResponse(
        data = products.map(ProductResponseData.fromDomain),
        links = CollectionLinks("self", "next")
      )
    }
  }

}
