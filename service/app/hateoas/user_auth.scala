package object user_auth {

  case class UserAuthRequest(username: String, password: String)

  case class UserAuthResponse(token: String)

}
