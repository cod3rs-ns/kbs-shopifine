package com.dmarjanovic.drools

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

object DroolsService {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("drools-service-system")
    implicit val materializer = ActorMaterializer()

    implicit val executionContext = system.dispatcher

    val route =
      path("hello") {
        get {
          complete(HttpEntity(ContentTypes.`application/json`, "{\"message\": \"Hello, Drools\"}"))
        }
      }

    val config: Config = ConfigFactory.load()
    val host: String = config.getString("http.host")
    val port: Int = config.getInt("http.port")

    Http().bindAndHandle(handler = route, interface = host, port = port)
  }
}
