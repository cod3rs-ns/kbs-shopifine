package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{Error, ErrorResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import repositories.UserRepository
import users.{UserRequest, UserResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Users @Inject()(users: UserRepository)(implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._

  def registerUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UserRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec => {
        users.save(spec.toDomain).map({ user =>
          Ok(Json.toJson(UserResponse.fromDomain(user)))
        })
      }
    )
  }

}
