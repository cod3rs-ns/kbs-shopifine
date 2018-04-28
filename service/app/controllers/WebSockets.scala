package controllers

import javax.inject.Singleton

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.google.inject.Inject
import commons.{Error, ErrorResponse}
import controllers.WebSockets._
import hateoas.JsonApi._
import org.apache.http.HttpHeaders
import play.api.libs.json.{JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Controller, RequestHeader, WebSocket}
import repositories.UserRepository
import util.{JwtPayload, JwtUtil}
import ws.{Connector, NotificationBus}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WebSockets @Inject()(users: UserRepository,
                           notifications: NotificationBus,
                           jwt: JwtUtil)(implicit ec: ExecutionContext,
                                         ас: ActorSystem,
                                         mat: Materializer)
    extends Controller {

  def ws: WebSocket = WebSocket.acceptOrResult[String, String] { rh =>
    authorize(rh) match {
      case Some(f) =>
        f.map {
          case Some(user) =>
            Right(ActorFlow.actorRef { out =>
              Connector.props(user.id, notifications, out)
            })
          case _ => Left(Unauthorized(UnauthorizedRes))
        }
      case _ => Future.successful(Left(Unauthorized(UnauthorizedRes)))
    }
  }

  private def authorize(rh: RequestHeader) =
    rh.headers
      .get(HttpHeaders.AUTHORIZATION)
      .filter(jwt.isValid)
      .flatMap(jwt.decodePayload)
      .flatMap(pl => Json.parse(pl).validate[JwtPayload].asOpt)
      .map(vpl => users.findByUsername(vpl.username))

}

object WebSockets {

  val UnauthorizedRes: JsValue = Json.toJson(
    ErrorResponse(errors = Seq(Error("401", "Invalid credentials."))))

}
