import domain.{User, UserRole}
import org.joda.time.DateTime
import relationships.{
  RelationshipData,
  RelationshipLinks,
  RequestRelationship,
  ResponseRelationship
}

package object users {

  import hateoas._

  case class UserRequestAttributes(username: String,
                                   password: String,
                                   firstName: String,
                                   lastName: String,
                                   role: String,
                                   address: Option[String],
                                   latitude: Option[Double],
                                   longitude: Option[Double])

  case class UserRequestRelationships(buyerCategory: RequestRelationship)

  case class UserRequestData(`type`: String,
                             attributes: UserRequestAttributes,
                             relationships: Option[UserRequestRelationships])

  case class UserRequest(data: UserRequestData) {
    def toDomain: User = {
      val attributes    = data.attributes
      val relationships = data.relationships
      val isCustomer    = relationships.isDefined && attributes.role.toUpperCase == UserRole.CUSTOMER.name.toUpperCase

      User(
        username = attributes.username,
        password = attributes.password,
        firstName = attributes.firstName,
        lastName = attributes.lastName,
        role = UserRole.valueOf(attributes.role.toUpperCase),
        address = attributes.address,
        longitude = attributes.longitude,
        latitude = attributes.latitude,
        buyerCategoryId = if (isCustomer) Some(relationships.get.buyerCategory.data.id) else None,
        points = if (isCustomer) Some(0) else None,
        registeredAt = DateTime.now
      )
    }
  }

  case class UpdateUserRequestAttributes(firstName: Option[String],
                                         lastName: Option[String],
                                         address: Option[String],
                                         longitude: Option[Double],
                                         latitude: Option[Double])

  case class UpdateUserRequestData(`type`: String, attributes: UpdateUserRequestAttributes)

  case class UpdateUserRequest(data: UpdateUserRequestData) {
    def toDomain(user: User): User = {
      val attributes = data.attributes

      user.copy(
        firstName = attributes.firstName.getOrElse(user.firstName),
        lastName = attributes.lastName.getOrElse(user.lastName),
        address = attributes.address.orElse(user.address),
        longitude = attributes.longitude.orElse(user.longitude),
        latitude = attributes.latitude.orElse(user.latitude)
      )
    }
  }

  case class UserResponseAttributes(username: String,
                                    firstName: String,
                                    lastName: String,
                                    role: String,
                                    address: Option[String],
                                    longitude: Option[Double],
                                    latitude: Option[Double],
                                    points: Option[Long])

  case class UserResponseRelationships(buyerCategory: ResponseRelationship)

  case class UserResponseMeta(registeredAt: String)

  case class UserResponseData(`type`: String,
                              id: Long,
                              attributes: UserResponseAttributes,
                              meta: UserResponseMeta,
                              relationships: Option[UserResponseRelationships] = None)

  object UserResponseData {
    def fromDomain(user: User): UserResponseData = {
      val attributes = UserResponseAttributes(
        username = user.username,
        firstName = user.firstName,
        lastName = user.lastName,
        role = user.role.toString,
        address = user.address,
        longitude = user.longitude,
        latitude = user.latitude,
        points = user.points
      )

      val relationships =
        if (user.role == UserRole.CUSTOMER)
          Some(
            UserResponseRelationships(
              buyerCategory = ResponseRelationship(
                links = RelationshipLinks(
                  related = s"/api/users/${user.id.get}/categories/${user.buyerCategoryId.get}"
                ),
                data = RelationshipData(BuyerCategoriesType, user.buyerCategoryId.get)
              )))
        else None

      UserResponseData(
        `type` = UsersType,
        id = user.id.get,
        attributes = attributes,
        meta = UserResponseMeta(registeredAt = user.registeredAt.toString),
        relationships = relationships
      )
    }
  }

  case class UserResponse(data: UserResponseData)

  object UserResponse {
    def fromDomain(user: User): UserResponse =
      UserResponse(data = UserResponseData.fromDomain(user))
  }

}
