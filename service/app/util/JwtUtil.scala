package util

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.json.Json

case class JwtPayload(iss: String, iat: Long, exp: Long, aud: String, sub: String, username: String, role: String)

class JwtUtil @Inject()(config: Configuration) {

  import hateoas.JsonApi._

  val JwtSecretKey: String = config.getString("jwt.secretKey").get
  val JwtAlgorithm: String = config.getString("jwt.algorithm").get

  def createToken(payload: JwtPayload): String = {
    val header = JwtHeader(JwtAlgorithm)
    val claimsSet = JwtClaimsSet(Json.toJson(payload).toString)

    JsonWebToken(header, claimsSet, JwtSecretKey)
  }

  def isValid(token: String): Boolean = {
    val parts = token.split(" ")
    2 == parts.length && "Bearer" == parts(0) && JsonWebToken.validate(parts(1), JwtSecretKey)
  }

  def decodePayload(token: String): Option[String] =
    token.split(" ")(1) match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asJsonString)
      case _ => None
    }

}
