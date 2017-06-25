package hateoas

import bills._
import play.api.libs.json.{Json, OFormat}
import commons.CollectionLinks
import hateoas.buyer_categories._
import relationships._
import products._
import users._

object JsonApi {

  implicit val collectionLinks: OFormat[CollectionLinks] = Json.format[CollectionLinks]

  implicit val relData: OFormat[RelationshipData] = Json.format[RelationshipData]
  implicit val relLinks: OFormat[RelationshipLinks] = Json.format[RelationshipLinks]
  implicit val reqRels: OFormat[RequestRelationship] = Json.format[RequestRelationship]
  implicit val resRels: OFormat[ResponseRelationship] = Json.format[ResponseRelationship]

  implicit val prodReqAttrs: OFormat[ProductRequestAttributes] = Json.format[ProductRequestAttributes]
  implicit val prodReqRels: OFormat[ProductRequestRelationships] = Json.format[ProductRequestRelationships]
  implicit val prodReqData: OFormat[ProductRequestData] = Json.format[ProductRequestData]
  implicit val prodReq: OFormat[ProductRequest] = Json.format[ProductRequest]
  implicit val prodResAttrs: OFormat[ProductResponseAttributes] = Json.format[ProductResponseAttributes]
  implicit val prodResRels: OFormat[ProductResponseRelationships] = Json.format[ProductResponseRelationships]
  implicit val prodResData: OFormat[ProductResponseData] = Json.format[ProductResponseData]
  implicit val prodRes: OFormat[ProductResponse] = Json.format[ProductResponse]
  implicit val prodResCollection: OFormat[ProductCollectionResponse] = Json.format[ProductCollectionResponse]

  implicit val userReqAttrs: OFormat[UserRequestAttributes] = Json.format[UserRequestAttributes]
  implicit val userReqRels: OFormat[UserRequestRelationships] = Json.format[UserRequestRelationships]
  implicit val userReqData: OFormat[UserRequestData] = Json.format[UserRequestData]
  implicit val userReq: OFormat[UserRequest] = Json.format[UserRequest]
  implicit val userResAttrs: OFormat[UserResponseAttributes] = Json.format[UserResponseAttributes]
  implicit val userResRels: OFormat[UserResponseRelationships] = Json.format[UserResponseRelationships]
  implicit val userResMeta: OFormat[UserResponseMeta] = Json.format[UserResponseMeta]
  implicit val userResData: OFormat[UserResponseData] = Json.format[UserResponseData]
  implicit val userRes: OFormat[UserResponse] = Json.format[UserResponse]

  implicit val billReqAttrs: OFormat[BillRequestAttributes] = Json.format[BillRequestAttributes]
  implicit val billReqRels: OFormat[BillRequestRelationships] = Json.format[BillRequestRelationships]
  implicit val billReqData: OFormat[BillRequestData] = Json.format[BillRequestData]
  implicit val billReq: OFormat[BillRequest] = Json.format[BillRequest]
  implicit val billResAttrs: OFormat[BillResponseAttributes] = Json.format[BillResponseAttributes]
  implicit val billResRels: OFormat[BillResponseRelationships] = Json.format[BillResponseRelationships]
  implicit val billResData: OFormat[BillResponseData] = Json.format[BillResponseData]
  implicit val billRes: OFormat[BillResponse] = Json.format[BillResponse]
  implicit val billResCollection: OFormat[BillCollectionResponse] = Json.format[BillCollectionResponse]

  implicit val buyerCatReqAttrs: OFormat[BuyerCategoryRequestAttributes] = Json.format[BuyerCategoryRequestAttributes]
  implicit val buyerCatReqData: OFormat[BuyerCategoryRequestData] = Json.format[BuyerCategoryRequestData]
  implicit val buyerCatReq: OFormat[BuyerCategoryRequest] = Json.format[BuyerCategoryRequest]
  implicit val buyerCatResAttrs: OFormat[BuyerCategoryResponseAttributes] = Json.format[BuyerCategoryResponseAttributes]
  implicit val buyerCatResData: OFormat[BuyerCategoryResponseData] = Json.format[BuyerCategoryResponseData]
  implicit val buyerCatRes: OFormat[BuyerCategoryResponse] = Json.format[BuyerCategoryResponse]
  implicit val buyerCatCollection: OFormat[BuyerCategoryCollectionResponse] = Json.format[BuyerCategoryCollectionResponse]
  implicit val thresholdsAttrs: OFormat[ConsumptionThresholdAttributes] = Json.format[ConsumptionThresholdAttributes]
  implicit val thresholdsReqData: OFormat[ConsumptionThresholdRequestData] = Json.format[ConsumptionThresholdRequestData]
  implicit val thresholdsReq: OFormat[ConsumptionThresholdRequest] = Json.format[ConsumptionThresholdRequest]
  implicit val thresholdsResData: OFormat[ConsumptionThresholdResponseData] = Json.format[ConsumptionThresholdResponseData]
  implicit val thresholdsRes: OFormat[ConsumptionThresholdResponse] = Json.format[ConsumptionThresholdResponse]
  implicit val thresholdsResCollection: OFormat[ConsumptionThresholdCollectionResponse] = Json.format[ConsumptionThresholdCollectionResponse]

}
