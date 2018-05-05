package object google_auth {

  case class GoogleAuthRequest(idToken: String)

  case class GoogleAuthResponse(aud: String,
                                sub: String,
                                email: String,
                                email_verified: String,
                                exp: String,
                                picture: String,
                                given_name: String,
                                family_name: String)
}
