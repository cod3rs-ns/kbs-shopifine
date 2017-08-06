package com.dmarjanovic.drools

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.dmarjanovic.drools.hateoas._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

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

  implicit val productAttr: RootJsonFormat[ProductResponseAttributes] = jsonFormat8(ProductResponseAttributes)
  implicit val productRel: RootJsonFormat[ProductResponseRelationships] = jsonFormat2(ProductResponseRelationships)
  implicit val productData: RootJsonFormat[ProductResponseData] = jsonFormat4(ProductResponseData)
  implicit val productRes: RootJsonFormat[ProductResponse] = jsonFormat1(ProductResponse)
  implicit val productCollRes: RootJsonFormat[ProductCollectionResponse] = jsonFormat2(ProductCollectionResponse)

  implicit val billResAttrs: RootJsonFormat[BillResponseAttributes] = jsonFormat7(BillResponseAttributes)
  implicit val billResRels: RootJsonFormat[BillResponseRelationships] = jsonFormat3(BillResponseRelationships)
  implicit val billResData: RootJsonFormat[BillResponseData] = jsonFormat4(BillResponseData)
  implicit val billRes: RootJsonFormat[BillResponse] = jsonFormat1(BillResponse)

  implicit val billItemReqAttr: RootJsonFormat[BillItemRequestAttributes] = jsonFormat3(BillItemRequestAttributes)
  implicit val billItemReqRels: RootJsonFormat[BillItemRequestRelationships] = jsonFormat2(BillItemRequestRelationships)
  implicit val billItemReqData: RootJsonFormat[BillItemRequestData] = jsonFormat3(BillItemRequestData)
  implicit val billItemReq: RootJsonFormat[BillItemRequest] = jsonFormat1(BillItemRequest)

}
