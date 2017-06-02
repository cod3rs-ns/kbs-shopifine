import Dependencies._
import sbt.Keys._

lazy val buildSettings = Seq(
  name := "Shopifine service",
  organization := "com.dmarjanovic",
  version := "1.0.0",
  scalaVersion := "2.11.11"
)

lazy val coreLibs = Seq(mysql, slick, slickEvolutions)

lazy val testLibs = Seq(scalaTestPlus)

libraryDependencies += filters

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(buildSettings: _*)
  .settings(
    libraryDependencies ++= (coreLibs ++ testLibs)
  )
