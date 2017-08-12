package com.dmarjanovic.drools

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.dmarjanovic.drools.hateoas._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val discountRes: RootJsonFormat[DiscountResponse] = jsonFormat2(DiscountResponse)
  implicit val billWithDiscounts: RootJsonFormat[BillWithDiscountsResponse] = jsonFormat6(BillWithDiscountsResponse)
  implicit val billItemWithDiscounts: RootJsonFormat[BillItemWithDiscountsResponse] = jsonFormat5(BillItemWithDiscountsResponse)

  implicit val relData: RootJsonFormat[RelationshipData] = jsonFormat2(RelationshipData)
  implicit val relLinks: RootJsonFormat[RelationshipLinks] = jsonFormat1(RelationshipLinks)
  implicit val reqRel: RootJsonFormat[RequestRelationship] = jsonFormat1(RequestRelationship)
  implicit val resRel: RootJsonFormat[ResponseRelationship] = jsonFormat2(ResponseRelationship)
  implicit val resRelColl: RootJsonFormat[ResponseRelationshipCollection] = jsonFormat1(ResponseRelationshipCollection)

  implicit val collLinks: RootJsonFormat[CollectionLinks] = jsonFormat3(CollectionLinks)

  implicit val productCatResAttrs: RootJsonFormat[ProductCategoryResponseAttributes] = jsonFormat3(ProductCategoryResponseAttributes)
  implicit val productCatResRels: RootJsonFormat[ProductCategoryResponseRelationships] = jsonFormat2(ProductCategoryResponseRelationships)
  implicit val productCatResData: RootJsonFormat[ProductCategoryResponseData] = jsonFormat4(ProductCategoryResponseData)
  implicit val productCatRes: RootJsonFormat[ProductCategoryResponse] = jsonFormat1(ProductCategoryResponse)

  implicit val productAttr: RootJsonFormat[ProductResponseAttributes] = jsonFormat3(ProductResponseAttributes)
  implicit val productRel: RootJsonFormat[ProductResponseRelationships] = jsonFormat2(ProductResponseRelationships)
  implicit val productData: RootJsonFormat[ProductResponseData] = jsonFormat4(ProductResponseData)
  implicit val productRes: RootJsonFormat[ProductResponse] = jsonFormat1(ProductResponse)
  implicit val productCollRes: RootJsonFormat[ProductCollectionResponse] = jsonFormat2(ProductCollectionResponse)

  implicit val billResAttrs: RootJsonFormat[BillResponseAttributes] = jsonFormat7(BillResponseAttributes)
  implicit val billResRels: RootJsonFormat[BillResponseRelationships] = jsonFormat3(BillResponseRelationships)
  implicit val billResData: RootJsonFormat[BillResponseData] = jsonFormat4(BillResponseData)
  implicit val billRes: RootJsonFormat[BillResponse] = jsonFormat1(BillResponse)

  implicit val billItemReqAttrs: RootJsonFormat[BillItemRequestAttributes] = jsonFormat3(BillItemRequestAttributes)
  implicit val billItemReqRels: RootJsonFormat[BillItemRequestRelationships] = jsonFormat2(BillItemRequestRelationships)
  implicit val billItemReqData: RootJsonFormat[BillItemRequestData] = jsonFormat3(BillItemRequestData)
  implicit val billItemReq: RootJsonFormat[BillItemRequest] = jsonFormat1(BillItemRequest)

  implicit val consumptionThresholdsAttrs: RootJsonFormat[ConsumptionThresholdAttributes] = jsonFormat3(ConsumptionThresholdAttributes)
  implicit val consumptionThresholdsRels: RootJsonFormat[ConsumptionThresholdRelationships] = jsonFormat1(ConsumptionThresholdRelationships)
  implicit val consumptionThresholdsData: RootJsonFormat[ConsumptionThresholdData] = jsonFormat4(ConsumptionThresholdData)
  implicit val consumptionThresholdsCollRes: RootJsonFormat[ConsumptionThresholdCollectionResponse] = jsonFormat2(ConsumptionThresholdCollectionResponse)

  implicit val buyerCategoryResAttrs: RootJsonFormat[BuyerCategoryResponseAttributes] = jsonFormat1(BuyerCategoryResponseAttributes)
  implicit val buyerCategoryResRels: RootJsonFormat[BuyerCategoryResponseRelationships] = jsonFormat1(BuyerCategoryResponseRelationships)
  implicit val buyerCategoryResData: RootJsonFormat[BuyerCategoryResponseData] = jsonFormat4(BuyerCategoryResponseData)
  implicit val buyerCategoryRes: RootJsonFormat[BuyerCategoryResponse] = jsonFormat1(BuyerCategoryResponse)

  implicit val customerResAttrs: RootJsonFormat[CustomerResponseAttributes] = jsonFormat6(CustomerResponseAttributes)
  implicit val customerResRels: RootJsonFormat[CustomerResponseRelationships] = jsonFormat1(CustomerResponseRelationships)
  implicit val customerResMeta: RootJsonFormat[CustomerResponseMeta] = jsonFormat1(CustomerResponseMeta)
  implicit val customerResData: RootJsonFormat[CustomerResponseData] = jsonFormat5(CustomerResponseData)
  implicit val customerRes: RootJsonFormat[CustomerResponse] = jsonFormat1(CustomerResponse)

}
