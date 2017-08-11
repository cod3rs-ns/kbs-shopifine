package com.dmarjanovic.drools.external

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dmarjanovic.drools.JsonSupport
import com.dmarjanovic.drools.domain.Product
import com.dmarjanovic.drools.hateoas.{ProductCollectionResponse, ProductResponse}

import scala.concurrent.Future

object ProductsProxy extends JsonSupport {

  def retrieveProduct(id: Long): Future[Product] = {
    val url: String = s"$CoreBaseUrl/api/products/$id"

    Http().singleRequest(HttpRequest(uri = url)
      .withHeaders(Authorization))
      .flatMap(response => {
        Unmarshal(response.entity).to[ProductResponse].flatMap(json =>
          json.toDomain
        )
      })
  }

  def retrieveProducts: Future[Seq[Product]] = {
    val url: String = s"$CoreBaseUrl/api/products"

    Http().singleRequest(HttpRequest(uri = url))
      .flatMap(response => {
        Unmarshal(response.entity).to[ProductCollectionResponse].map(json =>
          json.data.map(_.toDomain)
        )
      })
  }

}
