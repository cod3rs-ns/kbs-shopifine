import sbt._

object Dependencies {
  private val slickVersion = "2.0.0"

  val mysql: ModuleID =  "mysql" % "mysql-connector-java" % "5.1.42"
  val slick: ModuleID = "com.typesafe.play" %% "play-slick" % slickVersion
  val slickEvolutions: ModuleID = "com.typesafe.play" %% "play-slick-evolutions" % slickVersion

  val scalaTestPlus: ModuleID = "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
}
