package com.dmarjanovic.drools.external

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dmarjanovic.drools.JsonSupport
import com.dmarjanovic.drools.domain.User
import com.dmarjanovic.drools.hateoas.CustomerResponse

import scala.concurrent.Future

object CustomersProxy extends JsonSupport {

  def retrieveUser(id: Long): Future[User] = {
    val url: String = s"$CoreBaseUrl/api/users/$id"

    Http().singleRequest(HttpRequest(uri = url)
      .withHeaders(Authorization))
      .flatMap(response => {
        Unmarshal(response.entity).to[CustomerResponse].map(json =>
          json.toDomain
        )
      })
  }

}
