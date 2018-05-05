package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{Error, ErrorResponse}
import controllers.Users._
import domain.User
import google_auth.GoogleAuthRequest
import hateoas.JsonApi._
import org.apache.http.HttpStatus._
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.UserRepository
import services.GoogleAuthService
import user_auth.{UserAuthRequest, UserAuthResponse}
import users.{UserRequest, UserResponse}
import util.{JwtPayload, JwtUtil}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Users @Inject()(ws: WSClient,
                      users: UserRepository,
                      jwt: JwtUtil,
                      secure: SecuredAuthenticator,
                      googleAuthService: GoogleAuthService)(implicit val ec: ExecutionContext)
    extends Controller {

  import secure.Roles.PermitAll

  def registerUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[UserRequest]
      .fold(
        _ => Future.successful(BadRequest(MalformedJson)),
        spec => {
          val user = spec.toDomain
          users.findByUsername(user.username).flatMap {
            case Some(_) =>
              Future.successful(
                Conflict(Json.toJson(
                  ErrorResponse(errors = Seq(Error(CONFLICT.toString, "Username already exists.")))
                )))

            case None =>
              users.save(user).map { registered =>
                Created(Json.toJson(UserResponse.fromDomain(registered)))
              }
          }
        }
      )
  }

  def retrieveOne(id: Long): Action[AnyContent] =
    secure.AuthWith(PermitAll).async {
      users.retrieve(id) map {
        case Some(user) => Ok(Json.toJson(UserResponse.fromDomain(user)))

        case None =>
          NotFound(
            Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"User $id doesn't exist!")))))
      }
    }

  def auth(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[UserAuthRequest]
      .fold(
        _ => Future.successful(BadRequest(MalformedJson)),
        auth =>
          users.findByUsernameAndPassword(auth.username, auth.password) map {
            case Some(user) =>
              val token = createToken(user)
              Ok(Json.toJson(UserAuthResponse(token)))

            case None =>
              Unauthorized(
                Json.toJson(ErrorResponse(
                  errors = Seq(Error(UNAUTHORIZED.toString, "Wrong credentials provided.")))))
        }
      )
  }

  def googleAuth(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body
      .validate[GoogleAuthRequest]
      .fold(
        _ => Future.successful(BadRequest(MalformedJson)),
        tokenInfo =>
          googleAuthService
            .authenticate(tokenInfo.idToken)
            .map { user =>
              val token = createToken(user)
              Ok(Json.toJson(UserAuthResponse(token)))
            }
            .recoverWith {
              case ex: IllegalArgumentException =>
                Future.successful(BadRequest(Json.toJson(
                  ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, ex.getMessage))))))
          }
      )
  }

  private def createToken(user: User) = {
    val issuedAt = DateTime.now

    jwt.createToken(
      JwtPayload(
        iss = "issuer-of-token",
        iat = issuedAt.getMillis,
        exp = issuedAt.plusMinutes(15).getMillis,
        aud = "audience-of-token",
        sub = "subject-of-token",
        id = user.id.get,
        username = user.username,
        role = user.role.toString
      ))
  }

}

object Users {

  private val MalformedJson = Json.toJson(
    ErrorResponse(errors = Seq(Error(SC_BAD_REQUEST.toString, "Malformed JSON specified."))))

}
