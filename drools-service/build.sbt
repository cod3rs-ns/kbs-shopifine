import Dependencies._

lazy val buildSettings = Seq(
  name := "Drools Service",
  version := "1.0.0",
  organization := "com.dmarjanovic",
  scalaVersion := "2.11.11"
)

lazy val coreLibs = Seq(akkaHttp)

lazy val utils = Seq(akkaLogger, logback, logbackColorizer)

lazy val root = (project in file("."))
  .settings(buildSettings: _*)
  .settings(
    libraryDependencies ++= (coreLibs ++ utils)
  )