package hateoas

import bills._
import commons.{CollectionLinks, Error, ErrorResponse, Meta}
import google_auth.{GoogleAuthRequest, GoogleAuthResponse}
import hateoas.action_discounts._
import hateoas.bill_discounts._
import hateoas.bill_item_discounts._
import hateoas.bill_items._
import hateoas.buyer_categories._
import hateoas.drools_service._
import hateoas.product_categories._
import play.api.libs.json.{Json, OFormat}
import products._
import relationships._
import user_auth.{UserAuthRequest, UserAuthResponse}
import users._
import util.JwtPayload

object JsonApi {

  implicit val collectionLinks: OFormat[CollectionLinks] = Json.format[CollectionLinks]
  implicit val error: OFormat[Error] = Json.format[Error]
  implicit val meta: OFormat[Meta] = Json.format[Meta]
  implicit val errorResponse: OFormat[ErrorResponse] = Json.format[ErrorResponse]

  implicit val relData: OFormat[RelationshipData] = Json.format[RelationshipData]
  implicit val relLinks: OFormat[RelationshipLinks] = Json.format[RelationshipLinks]
  implicit val reqRels: OFormat[RequestRelationship] = Json.format[RequestRelationship]
  implicit val resRels: OFormat[ResponseRelationship] = Json.format[ResponseRelationship]
  implicit val resRelsCollection: OFormat[ResponseRelationshipCollection] = Json.format[ResponseRelationshipCollection]

  implicit val prodReqAttrs: OFormat[ProductRequestAttributes] = Json.format[ProductRequestAttributes]
  implicit val prodReqRels: OFormat[ProductRequestRelationships] = Json.format[ProductRequestRelationships]
  implicit val prodReqData: OFormat[ProductRequestData] = Json.format[ProductRequestData]
  implicit val prodReq: OFormat[ProductRequest] = Json.format[ProductRequest]
  implicit val prodResAttrs: OFormat[ProductResponseAttributes] = Json.format[ProductResponseAttributes]
  implicit val prodResRels: OFormat[ProductResponseRelationships] = Json.format[ProductResponseRelationships]
  implicit val prodResData: OFormat[ProductResponseData] = Json.format[ProductResponseData]
  implicit val prodRes: OFormat[ProductResponse] = Json.format[ProductResponse]
  implicit val prodResCollection: OFormat[ProductCollectionResponse] = Json.format[ProductCollectionResponse]

  implicit val prodCatReqAttrs: OFormat[ProductCategoryRequestAttributes] = Json.format[ProductCategoryRequestAttributes]
  implicit val prodCatReqRels: OFormat[ProductCategoryRequestRelationships] = Json.format[ProductCategoryRequestRelationships]
  implicit val prodCatReqData: OFormat[ProductCategoryRequestData] = Json.format[ProductCategoryRequestData]
  implicit val prodCatReq: OFormat[ProductCategoryRequest] = Json.format[ProductCategoryRequest]
  implicit val prodCatResAttrs: OFormat[ProductCategoryResponseAttributes] = Json.format[ProductCategoryResponseAttributes]
  implicit val prodCatResRels: OFormat[ProductCategoryResponseRelationships] = Json.format[ProductCategoryResponseRelationships]
  implicit val prodCatResData: OFormat[ProductCategoryResponseData] = Json.format[ProductCategoryResponseData]
  implicit val prodCatRes: OFormat[ProductCategoryResponse] = Json.format[ProductCategoryResponse]
  implicit val prodCatResCollection: OFormat[ProductCategoryCollectionResponse] = Json.format[ProductCategoryCollectionResponse]

  implicit val userReqAttrs: OFormat[UserRequestAttributes] = Json.format[UserRequestAttributes]
  implicit val userReqRels: OFormat[UserRequestRelationships] = Json.format[UserRequestRelationships]
  implicit val userReqData: OFormat[UserRequestData] = Json.format[UserRequestData]
  implicit val userReq: OFormat[UserRequest] = Json.format[UserRequest]
  implicit val updateUserReqAttrs: OFormat[UpdateUserRequestAttributes] = Json.format[UpdateUserRequestAttributes]
  implicit val updateUserReqData: OFormat[UpdateUserRequestData] = Json.format[UpdateUserRequestData]
  implicit val updateUserReq: OFormat[UpdateUserRequest] = Json.format[UpdateUserRequest]
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

  implicit val billDiscountAttrs: OFormat[BillDiscountAttributes] = Json.format[BillDiscountAttributes]
  implicit val billDiscountRels: OFormat[BillDiscountRelationships] = Json.format[BillDiscountRelationships]
  implicit val billDiscountData: OFormat[BillDiscountData] = Json.format[BillDiscountData]
  implicit val billDiscountRes: OFormat[BillDiscountResponse] = Json.format[BillDiscountResponse]
  implicit val billDiscountResCollection: OFormat[BillDiscountCollectionResponse] = Json.format[BillDiscountCollectionResponse]

  implicit val billItemReqAttrs: OFormat[BillItemRequestAttributes] = Json.format[BillItemRequestAttributes]
  implicit val billItemReqRels: OFormat[BillItemRequestRelationships] = Json.format[BillItemRequestRelationships]
  implicit val billItemReqData: OFormat[BillItemRequestData] = Json.format[BillItemRequestData]
  implicit val billItemReq: OFormat[BillItemRequest] = Json.format[BillItemRequest]
  implicit val billItemResAttrs: OFormat[BillItemResponseAttributes] = Json.format[BillItemResponseAttributes]
  implicit val billItemResRels: OFormat[BillItemResponseRelationships] = Json.format[BillItemResponseRelationships]
  implicit val billItemResData: OFormat[BillItemResponseData] = Json.format[BillItemResponseData]
  implicit val billItemRes: OFormat[BillItemResponse] = Json.format[BillItemResponse]
  implicit val billItemResCollection: OFormat[BillItemCollectionResponse] = Json.format[BillItemCollectionResponse]

  implicit val billItemDiscountAttrs: OFormat[BillItemDiscountAttributes] = Json.format[BillItemDiscountAttributes]
  implicit val billItemDiscountRels: OFormat[BillItemDiscountRelationships] = Json.format[BillItemDiscountRelationships]
  implicit val billItemDiscountData: OFormat[BillItemDiscountData] = Json.format[BillItemDiscountData]
  implicit val billItemDiscountRes: OFormat[BillItemDiscountResponse] = Json.format[BillItemDiscountResponse]
  implicit val billItemDiscountResCollection: OFormat[BillItemDiscountCollectionResponse] = Json.format[BillItemDiscountCollectionResponse]

  implicit val buyerCatReqAttrs: OFormat[BuyerCategoryRequestAttributes] = Json.format[BuyerCategoryRequestAttributes]
  implicit val buyerCatReqData: OFormat[BuyerCategoryRequestData] = Json.format[BuyerCategoryRequestData]
  implicit val buyerCatReq: OFormat[BuyerCategoryRequest] = Json.format[BuyerCategoryRequest]
  implicit val buyerCatResAttrs: OFormat[BuyerCategoryResponseAttributes] = Json.format[BuyerCategoryResponseAttributes]
  implicit val buyerCatResRels: OFormat[BuyerCategoryResponseRelationships] = Json.format[BuyerCategoryResponseRelationships]
  implicit val buyerCatResData: OFormat[BuyerCategoryResponseData] = Json.format[BuyerCategoryResponseData]
  implicit val buyerCatRes: OFormat[BuyerCategoryResponse] = Json.format[BuyerCategoryResponse]
  implicit val buyerCatCollection: OFormat[BuyerCategoryCollectionResponse] = Json.format[BuyerCategoryCollectionResponse]
  implicit val thresholdsAttrs: OFormat[ConsumptionThresholdAttributes] = Json.format[ConsumptionThresholdAttributes]
  implicit val thresholdReqRels: OFormat[ConsumptionThresholdRequestRelationships] = Json.format[ConsumptionThresholdRequestRelationships]
  implicit val thresholdsReqData: OFormat[ConsumptionThresholdRequestData] = Json.format[ConsumptionThresholdRequestData]
  implicit val thresholdsReq: OFormat[ConsumptionThresholdRequest] = Json.format[ConsumptionThresholdRequest]
  implicit val thresholdResRels: OFormat[ConsumptionThresholdResponseRelationships] = Json.format[ConsumptionThresholdResponseRelationships]
  implicit val thresholdsResData: OFormat[ConsumptionThresholdResponseData] = Json.format[ConsumptionThresholdResponseData]
  implicit val thresholdsRes: OFormat[ConsumptionThresholdResponse] = Json.format[ConsumptionThresholdResponse]
  implicit val thresholdsResCollection: OFormat[ConsumptionThresholdCollectionResponse] = Json.format[ConsumptionThresholdCollectionResponse]

  implicit val actionDiscAttrs: OFormat[ActionDiscountAttributes] = Json.format[ActionDiscountAttributes]
  implicit val actionDiscReqData: OFormat[ActionDiscountRequestData] = Json.format[ActionDiscountRequestData]
  implicit val actionDiscReq: OFormat[ActionDiscountRequest] = Json.format[ActionDiscountRequest]
  implicit val actionDiscResRels: OFormat[ActionDiscountResponseRelationships] = Json.format[ActionDiscountResponseRelationships]
  implicit val actionDiscResData: OFormat[ActionDiscountResponseData] = Json.format[ActionDiscountResponseData]
  implicit val actionDiscRes: OFormat[ActionDiscountResponse] = Json.format[ActionDiscountResponse]
  implicit val actionDiscResCollection: OFormat[ActionDiscountCollectionResponse] = Json.format[ActionDiscountCollectionResponse]

  // Auth based classes
  implicit val jwtPayload: OFormat[JwtPayload] = Json.format[JwtPayload]
  implicit val userAuthReq: OFormat[UserAuthRequest] = Json.format[UserAuthRequest]
  implicit val userAuthRes: OFormat[UserAuthResponse] = Json.format[UserAuthResponse]
  implicit val googleAuthReq: OFormat[GoogleAuthRequest] = Json.format[GoogleAuthRequest]
  implicit val googleAuthRes: OFormat[GoogleAuthResponse] = Json.format[GoogleAuthResponse]

  // Drools Service responses
  implicit val droolsDiscountRes: OFormat[DiscountResponse] = Json.format[DiscountResponse]
  implicit val droolsBillRes: OFormat[BillWithDiscountsResponse] = Json.format[BillWithDiscountsResponse]
  implicit val droolsBillItemRes: OFormat[BillItemWithDiscountsResponse] = Json.format[BillItemWithDiscountsResponse]

  implicit val droolsProdResAttrs: OFormat[DroolsProductResponseAttributes] = Json.format[DroolsProductResponseAttributes]
  implicit val droolsProdResRels: OFormat[DroolsProductResponseRelationships] = Json.format[DroolsProductResponseRelationships]
  implicit val droolsProdResData: OFormat[DroolsProductResponseData] = Json.format[DroolsProductResponseData]
  implicit val droolsProdResColl: OFormat[DroolsProductResponseCollection] = Json.format[DroolsProductResponseCollection]

}
