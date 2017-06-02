package hateoas

import play.api.libs.json.{Json, OFormat}
import commons.CollectionLinks
import relationships._
import products._

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



}
