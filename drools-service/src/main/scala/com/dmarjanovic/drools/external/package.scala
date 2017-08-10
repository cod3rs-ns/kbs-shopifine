package com.dmarjanovic.drools

import akka.actor.ActorSystem
import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor

package object external {

  implicit val system = ActorSystem("drools-service-system")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  private val config: Config = ConfigFactory.load()

  val CoreBaseUrl: String = config.getString("services.core.url")
  val CoreToken: String = config.getString("services.core.token")

  val Authorization = RawHeader("Authorization", s"Bearer $CoreToken")

}
