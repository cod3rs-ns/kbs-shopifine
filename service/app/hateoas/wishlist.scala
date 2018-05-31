import commons.CollectionLinks
import domain.WishlistItem
import hateoas.{ProductsType, UsersType, WishlistItemsType}
import org.joda.time.DateTime
import relationships._

package object wishlist {

  case class WishlistItemRequestAttributes()

  case class WishlistItemRequestRelationships(customer: RequestRelationship,
                                              product: RequestRelationship)

  case class WishlistItemRequestData(`type`: String,
                                     attributes: WishlistItemRequestAttributes,
                                     relationships: WishlistItemRequestRelationships)

  case class WishlistItemRequest(data: WishlistItemRequestData) {
    def toDomain: WishlistItem = {
      WishlistItem(
        createdAt = DateTime.now,
        customerId = data.relationships.customer.data.id,
        productId = data.relationships.product.data.id
      )
    }
  }

  case class WishlistItemResponseAttributes(createdAt: String)

  case class WishlistItemResponseRelationships(customer: ResponseRelationship,
                                               product: ResponseRelationship)

  case class WishlistItemResponseData(`type`: String,
                                      id: Long,
                                      attributes: WishlistItemResponseAttributes,
                                      relationships: WishlistItemResponseRelationships)

  object WishlistItemResponseData {
    def fromDomain(item: WishlistItem): WishlistItemResponseData = {
      val attributes = WishlistItemResponseAttributes(
        createdAt = item.createdAt.toString
      )

      val relationships = WishlistItemResponseRelationships(
        customer = ResponseRelationship(links = RelationshipLinks(
                                          related = s"/api/users/${item.customerId}"
                                        ),
                                        data = RelationshipData(
                                          `type` = UsersType,
                                          id = item.customerId
                                        )),
        product = ResponseRelationship(links = RelationshipLinks(
                                         related = s"/api/users/${item.productId}"
                                       ),
                                       data = RelationshipData(
                                         `type` = ProductsType,
                                         id = item.productId
                                       ))
      )

      WishlistItemResponseData(
        `type` = WishlistItemsType,
        id = item.id.get,
        attributes = attributes,
        relationships = relationships
      )
    }
  }

  case class WishlistItemResponse(data: WishlistItemResponseData)

  object WishlistItemResponse {
    def fromDomain(item: WishlistItem): WishlistItemResponse =
      WishlistItemResponse(data = WishlistItemResponseData.fromDomain(item))
  }

  case class WishlistItemCollectionResponse(data: Seq[WishlistItemResponseData],
                                            links: CollectionLinks)

  object WishlistItemCollectionResponse {
    def fromDomain(wishlist: Seq[WishlistItem],
                   links: CollectionLinks): WishlistItemCollectionResponse = {
      WishlistItemCollectionResponse(
        data = wishlist.map(WishlistItemResponseData.fromDomain),
        links = links
      )
    }
  }

}
