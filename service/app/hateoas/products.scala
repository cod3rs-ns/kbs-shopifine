import commons.CollectionLinks
import domain.Product
import org.joda.time.DateTime
import relationships._

package object products {

  import hateoas._

  case class ProductRequestAttributes(name: String,
                                      imageUrl: String,
                                      price: Double,
                                      quantity: Long,
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

      // TODO Fix if `fillStock` and `status` is defined
      Product(
        name = attributes.name,
        imageUrl = attributes.imageUrl,
        categoryId = categoryRel.id,
        price = attributes.price,
        quantity = attributes.quantity,
        createdAt = DateTime.now,
        minQuantity = attributes.minQuantity
      )
    }
  }

  case class ProductResponseAttributes(name: String,
                                       imageUrl: String,
                                       price: Double,
                                       quantity: Long,
                                       createdAt: String,
                                       lastBoughtAt: String,
                                       fillStock: Boolean,
                                       status: String,
                                       minQuantity: Long)

  case class ProductResponseRelationships(category: ResponseRelationship, discounts: ResponseRelationshipCollection)

  case class ProductResponseData(`type`: String,
                                 id: Long,
                                 attributes: ProductResponseAttributes,
                                 relationships: ProductResponseRelationships)

  object ProductResponseData {
    def fromDomain(product: Product): ProductResponseData = {
      val attributes = ProductResponseAttributes(
        name = product.name,
        imageUrl = product.imageUrl,
        price = product.price,
        quantity = product.quantity,
        createdAt = product.createdAt.toString,
        lastBoughtAt = if (product.lastBoughtAt.isDefined) product.lastBoughtAt.get.toString else "Never",
        fillStock = product.fillStock,
        status = product.status.name,
        minQuantity = product.minQuantity
      )

      val relationships = ProductResponseRelationships(
        category = ResponseRelationship(
          links = RelationshipLinks(
            related = s"/api/products/${product.id.get}/product-categories/${product.categoryId}"
          ),
          data = RelationshipData(ProductCategories, product.categoryId)
        ),
        discounts = ResponseRelationshipCollection(
          links = RelationshipLinks(
            related = s"/api/product-categories/${product.categoryId}/action-discounts"
          )
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
    def fromDomain(products: Seq[Product], links: CollectionLinks): ProductCollectionResponse = {
      ProductCollectionResponse(
        data = products.map(ProductResponseData.fromDomain),
        links = links
      )
    }
  }

}
