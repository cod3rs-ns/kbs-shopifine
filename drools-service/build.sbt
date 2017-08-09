import Dependencies._
import Resolvers._

lazy val buildSettings = Seq(
  name := "Drools Service",
  version := "1.0.0",
  organization := "com.dmarjanovic",
  scalaVersion := "2.11.11"
)

lazy val coreLibs = Seq(akkaHttp, xstream)

lazy val droolsLibs = Seq(droolsCore, droolsCompiler, droolsJsr, droolsDecisionTables, droolsKnowledgeApi)

lazy val utils = Seq(akkaLogger, logback, logbackColorizer, jodaTime, sprayJson)

lazy val root = (project in file("."))
  .settings(buildSettings: _*)
  .settings(
    resolvers ++= Seq(jBoss),
    libraryDependencies ++= (coreLibs ++ droolsLibs ++ utils)
  )