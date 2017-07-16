package hateoas

import commons.CollectionLinks
import domain.BillItem
import relationships.{RelationshipData, RelationshipLinks, RequestRelationship, ResponseRelationship}

package object bill_items {

  case class BillItemRequestAttributes(price: Double, quantity: Int, discount: Double)

  case class BillItemRequestRelationships(product: RequestRelationship, bill: RequestRelationship)

  case class BillItemRequestData(`type`: String, attributes: BillItemRequestAttributes, relationships: BillItemRequestRelationships)

  case class BillItemRequest(data: BillItemRequestData) {
    def toDomain: BillItem = {
      val attributes = data.attributes
      val relationships = data.relationships
      val amount = attributes.price * attributes.quantity

      BillItem(
        ordinal = 0,
        productId = relationships.product.data.id,
        billId = relationships.bill.data.id,
        price = attributes.price,
        quantity = attributes.quantity,
        amount = amount,
        discount = attributes.discount,
        discountAmount = attributes.discount / 100.0 * amount
      )
    }
  }

  case class BillItemResponseAttributes(ordinal: Int, price: Double, quantity: Int, amount: Double, discount: Double, discountAmount: Double)

  case class BillItemResponseRelationships(product: ResponseRelationship, bill: ResponseRelationship)

  case class BillItemResponseData(id: Long, attributes: BillItemResponseAttributes, relationships: BillItemResponseRelationships)

  object BillItemResponseData {
    def fromDomain(billItem: BillItem, customer: Long): BillItemResponseData = {
      val attributes = BillItemResponseAttributes(
        ordinal = billItem.ordinal,
        price = billItem.price,
        quantity = billItem.quantity,
        amount = billItem.amount,
        discount = billItem.discount,
        discountAmount = billItem.discountAmount
      )

      val relationships = BillItemResponseRelationships(
        product = ResponseRelationship(
          data = RelationshipData(
            `type` = ProductsType,
            id = billItem.productId
          ),
          links = RelationshipLinks(
            related = s"api/products/${billItem.productId}"
          )
        ),
        bill = ResponseRelationship(
          data = RelationshipData(
            `type` = BillsType,
            id = billItem.billId
          ),
          links = RelationshipLinks(
            related = s"api/users/$customer/bills/${billItem.billId}"
          )
        )
      )

      BillItemResponseData(
        id = billItem.id.get,
        attributes = attributes,
        relationships = relationships
      )
    }
  }

  case class BillItemResponse(data: BillItemResponseData)

  object BillItemResponse {
    def fromDomain(billItem: BillItem, customer: Long): BillItemResponse = BillItemResponse(data = BillItemResponseData.fromDomain(billItem, customer))
  }

  case class BillItemCollectionResponse(data: Seq[BillItemResponseData], links: CollectionLinks)

  object BillItemCollectionResponse {
    def fromDomain(billItems: Seq[BillItem], customer: Long, links: CollectionLinks): BillItemCollectionResponse = {
      BillItemCollectionResponse(
        data = billItems.map(item => BillItemResponseData.fromDomain(item, customer)),
        links = links
      )
    }
  }

}
