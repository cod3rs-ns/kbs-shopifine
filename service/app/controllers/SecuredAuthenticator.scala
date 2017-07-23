package controllers

import com.google.inject.Inject
import commons.{Error, ErrorResponse}
import domain.{User, UserRole}
import play.api.libs.json.Json
import play.api.mvc._
import repositories.UserRepository
import util.{JwtPayload, JwtUtil}

import scala.concurrent.{ExecutionContext, Future}

case class AuthRequest[A](user: User, request: Request[A]) extends WrappedRequest(request)

class SecuredAuthenticator @Inject()(users: UserRepository, jwt: JwtUtil)
                                    (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._

  case class AuthWith(roles: Seq[String]) extends ActionBuilder[AuthRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthRequest[A]) => Future[Result]): Future[Result] = {
      val token = request.headers.get("Authorization").getOrElse("")

      if (jwt.isValid(token)) {
        jwt.decodePayload(token).fold(
          Future.successful(BadRequest(Json.toJson(
            ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Invalid token.")))
          )))
        ) { payload =>
          Json.parse(payload).validate[JwtPayload].fold(
            failures => {
              Future.successful(BadRequest(Json.toJson(
                ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Invalid token.")))
              )))
            },

            validPayload => {
              users.findByUsername(validPayload.username).flatMap {
                case Some(user) =>
                  val role = user.role.toString
                  if (role == validPayload.role && roles.contains(role))
                    block(AuthRequest(user, request))
                  else
                    Future.successful(Forbidden(Json.toJson(
                      ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
                    )))

                case None =>
                  Future.successful(Unauthorized(Json.toJson(
                    ErrorResponse(errors = Seq(Error(UNAUTHORIZED.toString, "Invalid credentials.")))
                  )))
              }
            }
          )
        }
      }
      else {
        Future.successful(Unauthorized(Json.toJson(
          ErrorResponse(errors = Seq(Error(UNAUTHORIZED.toString, "Invalid credentials.")))
        )))
      }
    }
  }

  object Roles {
    val PermitAll: Seq[String] = UserRole.values().map(role => role.toString)
    val Customer: String = UserRole.CUSTOMER.toString
    val SalesManager: String = UserRole.SALES_MANAGER.toString
    val Salesman: String = UserRole.SALESMAN.toString
  }

}
