package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.{Product, ProductStatus}
import org.joda.time.DateTime

case class ProductResponseAttributes(name: String,
                                     imageUrl: String,
                                     price: Double,
                                     quantity: Long,
                                     createdAt: String,
                                     fillStock: Boolean,
                                     status: String,
                                     minQuantity: Long)

case class ProductResponseRelationships(category: ResponseRelationship, discounts: ResponseRelationshipCollection)

case class ProductResponseData(`type`: String,
                               id: Long,
                               attributes: ProductResponseAttributes,
                               relationships: ProductResponseRelationships) {
  def toDomain: Product = {
    Product(
      id = Some(id),
      name = attributes.name,
      imageUrl = attributes.imageUrl,
      price = attributes.price,
      quantity = attributes.quantity,
      createdAt = DateTime.parse(attributes.createdAt),
      fillStock = attributes.fillStock,
      status = ProductStatus.valueOf(attributes.status),
      minQuantity = attributes.minQuantity
    )
  }
}

object ProductResponseDataJson {
  def fromDomain(product: Product): ProductResponseData = {
    val attributes = ProductResponseAttributes(
      name = product.name,
      imageUrl = product.imageUrl,
      price = product.price,
      quantity = product.quantity,
      createdAt = product.createdAt.toString,
      fillStock = product.fillStock,
      status = product.status.name,
      minQuantity = product.minQuantity
    )

    val relationships = ProductResponseRelationships(
      category = ResponseRelationship(
        links = RelationshipLinks(
          related = "FIXME"
        ),
        data = RelationshipData("FIXME", 1)
      ),
      discounts = ResponseRelationshipCollection(
        links = RelationshipLinks(
          related = "FIXME"
        )
      )
    )

    ProductResponseData(
      `type` = "products",
      id = product.id.get,
      attributes = attributes,
      relationships = relationships
    )
  }
}

case class ProductCollectionResponse(data: Seq[ProductResponseData], links: CollectionLinks)

object ProductCollectionResponseJson {
  def fromDomain(products: Seq[Product], links: CollectionLinks): ProductCollectionResponse = {
    ProductCollectionResponse(
      data = products.map(ProductResponseDataJson.fromDomain),
      links = links
    )
  }
}
