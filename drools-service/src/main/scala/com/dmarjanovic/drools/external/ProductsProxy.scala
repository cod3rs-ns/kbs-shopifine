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
          json.toDomain(fetchCategory = true)
        )
      })
  }

  def retrieveProducts(category: Option[Long] = None): Future[Seq[Product]] = {
    val filter = if (category.isDefined) s"?filter[category]=${category.get}" else ""
    val url: String = s"$CoreBaseUrl/api/products$filter"

    Http().singleRequest(HttpRequest(uri = url))
      .flatMap(response => {
        Unmarshal(response.entity).to[ProductCollectionResponse].map(json =>
          json.data.map(_.toDomain)
        )
      })
  }

}
