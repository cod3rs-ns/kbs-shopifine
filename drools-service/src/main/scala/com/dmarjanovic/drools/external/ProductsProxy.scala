package com.dmarjanovic.drools.external

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.dmarjanovic.drools.JsonSupport
import com.dmarjanovic.drools.domain.Product
import com.dmarjanovic.drools.external.BillsProxy.CoreToken
import com.dmarjanovic.drools.hateoas.ProductResponse

import scala.concurrent.{ExecutionContextExecutor, Future}

object ProductsProxy extends JsonSupport {

  implicit val system = ActorSystem("drools-service-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val CoreBaseUrl: String = "http://localhost:9000"

  def retrieveProduct(id: Long): Future[Product] = {
    val url: String = s"$CoreBaseUrl/api/products/$id"
    Http().singleRequest(HttpRequest(uri = url)
      .withHeaders(RawHeader("Authorization", s"Bearer $CoreToken")))
      .flatMap(response => {
        Unmarshal(response.entity).to[ProductResponse].flatMap(json =>
          json.toDomain
        )
      })
  }

}
