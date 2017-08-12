import Dependencies._
import sbt.Keys._

lazy val buildSettings = Seq(
  name := "Shopifine Core Service",
  organization := "com.dmarjanovic",
  version := "1.2.0",
  scalaVersion := "2.11.11"
)

lazy val coreLibs = Seq(mysql, slick, slickEvolutions, ws)

lazy val testLibs = Seq(scalaTestPlus)

lazy val utils = Seq(swaggerUI, scalaJWT)

libraryDependencies += filters

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SwaggerPlugin)
  .settings(buildSettings: _*)
  .settings(
    libraryDependencies ++= (coreLibs ++ testLibs ++ utils)
  )
