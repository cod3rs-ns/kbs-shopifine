package hateoas

import commons.CollectionLinks
import domain.ActionDiscount
import org.joda.time.DateTime
import relationships.{RelationshipLinks, ResponseRelationshipCollection}

package object action_discounts {

  case class ActionDiscountAttributes(name: String, from: String, to: String, discount: Double)

  case class ActionDiscountRequestData(`type`: String, attributes: ActionDiscountAttributes)

  case class ActionDiscountRequest(data: ActionDiscountRequestData) {
    def toDomain: ActionDiscount = {
      val attributes = data.attributes

      ActionDiscount(
        name = attributes.name,
        from = DateTime.parse(attributes.from),
        to = DateTime.parse(attributes.to),
        discount = attributes.discount
      )
    }
  }

  case class ActionDiscountResponseRelationships(categories: ResponseRelationshipCollection)

  case class ActionDiscountResponseData(`type`: String, id: Long, attributes: ActionDiscountAttributes, relationships: ActionDiscountResponseRelationships)

  object ActionDiscountResponseData {
    def fromDomain(actionDiscount: ActionDiscount): ActionDiscountResponseData = {
      ActionDiscountResponseData(
        `type` = ActionDiscountsType,
        id = actionDiscount.id.get,
        attributes = ActionDiscountAttributes(
          name = actionDiscount.name,
          from = actionDiscount.from.toString,
          to = actionDiscount.to.toString,
          discount = actionDiscount.discount
        ),
        relationships = ActionDiscountResponseRelationships(
          categories = ResponseRelationshipCollection(
            links = RelationshipLinks(
              related = s"api/action-discounts/${actionDiscount.id.get}/product-categories"
            )
          )
        )
      )
    }
  }

  case class ActionDiscountResponse(data: ActionDiscountResponseData)

  object ActionDiscountResponse {
    def fromDomain(actionDiscount: ActionDiscount): ActionDiscountResponse =
      ActionDiscountResponse(data = ActionDiscountResponseData.fromDomain(actionDiscount))
  }

  case class ActionDiscountCollectionResponse(data: Seq[ActionDiscountResponseData], links: CollectionLinks)

  object ActionDiscountCollectionResponse {
    def fromDomain(actionDiscounts: Seq[ActionDiscount], links: CollectionLinks): ActionDiscountCollectionResponse = {
      ActionDiscountCollectionResponse(
        data = actionDiscounts.map(ActionDiscountResponseData.fromDomain),
        links = links
      )
    }
  }

}
