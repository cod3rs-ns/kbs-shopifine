package com.dmarjanovic.drools.hateoas

import com.dmarjanovic.drools.domain.Product
import com.dmarjanovic.drools.external.ProductCategoriesProxy

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ProductResponseAttributes(quantity: Long, fillStock: Boolean, minQuantity: Long)

case class ProductResponseRelationships(category: ResponseRelationship, discounts: ResponseRelationshipCollection)

case class ProductResponseData(`type`: String,
                               id: Long,
                               attributes: ProductResponseAttributes,
                               relationships: ProductResponseRelationships) {
  def toDomain: Product = {
    Product(
      id = Some(id),
      quantity = attributes.quantity,
      fillStock = attributes.fillStock,
      minQuantity = attributes.minQuantity
    )
  }
}

object ProductResponseDataJson {
  def fromDomain(product: Product): ProductResponseData = {
    val attributes = ProductResponseAttributes(
      quantity = product.quantity,
      fillStock = product.fillStock,
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

case class ProductResponse(data: ProductResponseData) {
  def toDomain: Future[Product] = {
    ProductCategoriesProxy.retrieveCategory(data.relationships.category.data.id).flatMap(category =>
      Future.successful(
        Product(
          id = Some(data.id),
          category = Some(category),
          quantity = data.attributes.quantity,
          fillStock = data.attributes.fillStock,
          minQuantity = data.attributes.minQuantity
        )
      )
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
