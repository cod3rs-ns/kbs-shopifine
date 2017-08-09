import sbt._

object Dependencies {
  private val AkkaVersion = "2.4.18"
  private val AkkaHttpVersion = "10.0.6"
  private val DroolsVersion = "6.0.1.Final"

  val akkaHttp: ModuleID = "com.typesafe.akka" % "akka-http_2.11" % AkkaHttpVersion
  val akkaLogger: ModuleID = "com.typesafe.akka" % "akka-slf4j_2.11" % AkkaVersion

  val xstream: ModuleID = "com.thoughtworks.xstream" % "xstream" % "1.4.10"

  val droolsCore: ModuleID = "org.drools" % "drools-core" % DroolsVersion
  val droolsCompiler: ModuleID = "org.drools" % "drools-compiler" % DroolsVersion
  val droolsJsr: ModuleID = "org.drools" % "drools-jsr94" % DroolsVersion
  val droolsDecisionTables: ModuleID = "org.drools" % "drools-decisiontables" % DroolsVersion
  val droolsKnowledgeApi: ModuleID = "org.drools" % "knowledge-api" % DroolsVersion

  val logback: ModuleID = "ch.qos.logback" % "logback-classic" % "1.1.8"
  val logbackColorizer: ModuleID = "org.tuxdude.logback.extensions" % "logback-colorizer" % "1.0.1"
  val jodaTime: ModuleID = "joda-time" % "joda-time" % "2.3"
  val sprayJson: ModuleID = "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.8"

  val scalaTest: ModuleID =  "org.scalatest" % "scalatest_2.11" % "3.0.3" % Test
}