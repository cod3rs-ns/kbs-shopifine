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

  implicit val productAttr: RootJsonFormat[ProductResponseAttributes] = jsonFormat8(ProductResponseAttributes)
  implicit val productRel: RootJsonFormat[ProductResponseRelationships] = jsonFormat2(ProductResponseRelationships)
  implicit val productData: RootJsonFormat[ProductResponseData] = jsonFormat4(ProductResponseData)
  implicit val productCollRes: RootJsonFormat[ProductCollectionResponse] = jsonFormat2(ProductCollectionResponse)

}
