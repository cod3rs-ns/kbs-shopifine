package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{Error, ErrorResponse}
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.UserRepository
import user_auth.{UserAuthRequest, UserAuthResponse}
import users.{UserRequest, UserResponse}
import util.{JwtPayload, JwtUtil}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Users @Inject()(users: UserRepository, jwt: JwtUtil, secure: SecuredAuthenticator)
                     (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles.Customer

  def registerUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UserRequest].fold(
      _ => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec =>
        users.save(spec.toDomain).map({ user =>
          Created(Json.toJson(UserResponse.fromDomain(user)))
        })
    )
  }

  def retrieveOne(id: Long): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async {
    users.retrieve(id) map {
      case Some(user) =>
        Ok(Json.toJson(UserResponse.fromDomain(user)))

      case None =>
        NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"User $id doesn't exist!")))
        ))
    }
  }

  def auth(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UserAuthRequest].fold(
      _ =>
        Future.successful(BadRequest(Json.toJson(
          ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
        ))),

      auth => {
        users.findByUsernameAndPassword(auth.username, auth.password) map {
          case Some(user) =>
            val issuedAt = DateTime.now
            // FIXME Repair JWT common claim set
            val token = jwt.createToken(JwtPayload(
              iss = "issuer-of-token",
              iat = issuedAt.getMillis,
              exp = issuedAt.plusMinutes(15).getMillis,
              aud = "audience-of-token",
              sub = "subject-of-token",
              id = user.id.get,
              username = user.username,
              role = user.role.toString
            ))
            Ok(Json.toJson(UserAuthResponse(token)))

          case None =>
            Unauthorized(Json.toJson(
              ErrorResponse(errors = Seq(Error(UNAUTHORIZED.toString, "Wrong credentials provided.")))
            ))
        }
      }
    )
  }
}
