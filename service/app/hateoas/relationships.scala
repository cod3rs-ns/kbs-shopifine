package object relationships {

  case class RelationshipData(`type`: String, id: Long)

  case class RelationshipLinks(related: String)

  case class RequestRelationship(data: RelationshipData)

  case class ResponseRelationship(links: RelationshipLinks, data: RelationshipData)

  case class ResponseRelationshipCollection(links: RelationshipLinks)

}
