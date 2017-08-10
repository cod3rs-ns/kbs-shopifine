package com.dmarjanovic.drools.external

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dmarjanovic.drools.JsonSupport
import com.dmarjanovic.drools.domain.Bill
import com.dmarjanovic.drools.hateoas.BillResponse

import scala.concurrent.Future

object BillsProxy extends JsonSupport {

  def retrieveBill(userId: Long, id: Long): Future[Bill] = {
    val url: String = s"$CoreBaseUrl/api/users/$userId/bills/$id"

    Http().singleRequest(HttpRequest(uri = url)
      .withHeaders(Authorization))
      .flatMap(response => {
        Unmarshal(response.entity).to[BillResponse].flatMap(json =>
          Future.successful(json.toDomain)
        )
      })
  }

}
