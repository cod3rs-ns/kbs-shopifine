package object relationships {

  case class RelationshipData(`type`: String, id: Long)

  case class RelationshipLinks(self: String, related: String)

  case class RequestRelationship(data: RelationshipData)

  case class ResponseRelationship(links: RelationshipLinks, data: RelationshipData)

}
