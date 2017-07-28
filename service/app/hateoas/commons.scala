import org.joda.time.DateTime

package object commons {

  case class CollectionLinks(prev: Option[String] = None, self: String, next: Option[String] = None)

  case class Error(status: String, detail: String)

  case class Meta(createdAt: String = DateTime.now().toString)

  case class ErrorResponse(meta: Meta = Meta(), errors: Seq[Error])

}
