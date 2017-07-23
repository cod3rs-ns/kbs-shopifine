import sbt._

object Dependencies {
  private val slickVersion = "2.0.0"

  val mysql: ModuleID =  "mysql" % "mysql-connector-java" % "5.1.42"
  val slick: ModuleID = "com.typesafe.play" %% "play-slick" % slickVersion
  val slickEvolutions: ModuleID = "com.typesafe.play" %% "play-slick-evolutions" % slickVersion

  val scalaJWT: ModuleID = "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5"
  val swaggerUI: ModuleID = "org.webjars" % "swagger-ui" % "2.2.0"

  val scalaTestPlus: ModuleID = "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
}
