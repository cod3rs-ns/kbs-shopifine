package hateoas

import domain.ProductStatus
import org.joda.time.{DateTime, DateTimeUtils}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import products.{ProductRequest, ProductRequestAttributes, ProductRequestData, ProductRequestRelationships}
import relationships.{RelationshipData, RequestRelationship}

class ProductRequestSpec extends WordSpecLike with MustMatchers with BeforeAndAfterAll {

  override def beforeAll(): Unit = DateTimeUtils.setCurrentMillisFixed(DateTime.now.getMillis)

  override def afterAll(): Unit = DateTimeUtils.setCurrentMillisSystem()

  "Product" must {
    "be successfully created with provided request" in {
      val attributes = ProductRequestAttributes(
        name = "test-product",
        imageUrl = "test-product-image-url",
        price = 21.10,
        quantity = 4,
        fillStock = None,
        status = None,
        minQuantity = 1
      )

      val relationships = ProductRequestRelationships(
        category = RequestRelationship(
          data = RelationshipData(
            `type` = ProductCategories,
            id = 1
          )
        )
      )

      val data = ProductRequestData(
        `type` = ProductsType,
        attributes = attributes,
        relationships = relationships
      )

      ProductRequest(data).toDomain must have(
        'name (attributes.name),
        'imageUrl (attributes.imageUrl),
        'price (attributes.price),
        'quantity (attributes.quantity),
        'minQuantity (attributes.minQuantity),
        'fillStock (false),
        'status (ProductStatus.ACTIVE)
      )
    }
  }

}
