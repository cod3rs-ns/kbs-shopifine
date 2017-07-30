import sbt._

object Dependencies {
  private val AkkaVersion = "2.4.18"
  private val AkkaHttpVersion = "10.0.6"

  val akkaHttp: ModuleID = "com.typesafe.akka" % "akka-http_2.11" % AkkaHttpVersion
  val akkaLogger: ModuleID = "com.typesafe.akka" % "akka-slf4j_2.11" % AkkaVersion

  val logback: ModuleID = "ch.qos.logback" % "logback-classic" % "1.1.8"
  val logbackColorizer: ModuleID = "org.tuxdude.logback.extensions" % "logback-colorizer" % "1.0.1"
}