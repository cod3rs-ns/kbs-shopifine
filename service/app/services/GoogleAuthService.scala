package services

import javax.inject.Inject

import domain.{User, UserRole}
import google_auth.GoogleAuthResponse
import hateoas.JsonApi._
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.utils.URIBuilder
import org.joda.time.DateTime
import play.api.Configuration
import play.api.libs.ws.WSClient
import repositories.UserRepository
import services.GoogleAuthService._

import scala.concurrent.{ExecutionContext, Future}

class GoogleAuthService @Inject()(ws: WSClient,
                                  config: Configuration,
                                  userRepository: UserRepository)(implicit ex: ExecutionContext) {

  private val clientID = config.getString("google.client-id")

  def authenticate(idToken: String): Future[User] =
    validateToken(idToken).flatMap { res =>
      userRepository.findByGoogleId(res.sub).flatMap {
        case Some(u) => Future.successful(u)
        case _ =>
          val userToRegister = User(
            username = res.email,
            password = StringUtils.EMPTY,
            firstName = res.given_name,
            lastName = res.family_name,
            role = UserRole.CUSTOMER,
            address = None,
            points = None,
            buyerCategoryId = Some(SilverBuyerCategoryId),
            registeredAt = DateTime.now,
            googleAccountId = Option(res.sub)
          )

          userRepository.save(userToRegister)
      }
    }

  private def validateToken(idToken: String): Future[GoogleAuthResponse] = {
    val url = new URIBuilder(GoogleAuthUrl)
      .addParameter(IdTokenParam, idToken)
      .build()

    ws.url(url.toString)
      .get()
      .flatMap(
        _.json
          .validate[GoogleAuthResponse]
          .fold(
            _ => Future.failed(new IllegalArgumentException("Invalid token provided.")),
            res =>
              if (clientID.contains(res.aud)) {
                Future.successful(res)
              } else {
                Future.failed(new IllegalArgumentException("Invalid token provided."))
            }
          )
      )
  }
}

object GoogleAuthService {

  val GoogleAuthUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo"
  val IdTokenParam  = "id_token"

  val SilverBuyerCategoryId = 1

}
