package com.dmarjanovic.drools.external

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.dmarjanovic.drools.JsonSupport
import com.dmarjanovic.drools.domain.Bill
import com.dmarjanovic.drools.hateoas.BillResponse

import scala.concurrent.{ExecutionContextExecutor, Future}

object BillsProxy extends JsonSupport {

  implicit val system = ActorSystem("drools-service-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val CoreBaseUrl: String = "http://localhost:9000"
  val CoreToken: String = "681995"

  def retrieveBill(userId: Long, id: Long): Future[Bill] = {
    val url: String = s"$CoreBaseUrl/api/users/$userId/bills/$id"

    Http().singleRequest(HttpRequest(uri = url)
      .withHeaders(RawHeader("Authorization", s"Bearer $CoreToken")))
      .flatMap(response => {
        Unmarshal(response.entity).to[BillResponse].flatMap(json =>
          Future.successful(json.toDomain)
        )
      })
  }

}
