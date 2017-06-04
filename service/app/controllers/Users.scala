package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import users.{UserRequest, UserResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Users @Inject()(implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._

  def registerUser(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[UserRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson("Malformed JSON provided."))),

      spec => {
        // TODO Save user to database
        val user = spec.toDomain.copy(id = Some(1))
        Future.successful(Ok(Json.toJson(UserResponse.fromDomain(user))))
      }
    )
  }

}
