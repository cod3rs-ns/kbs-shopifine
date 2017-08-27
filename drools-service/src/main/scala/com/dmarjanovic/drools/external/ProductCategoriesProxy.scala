package com.dmarjanovic.drools.external

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dmarjanovic.drools.JsonSupport
import com.dmarjanovic.drools.domain.ProductCategory
import com.dmarjanovic.drools.hateoas.ProductCategoryResponse

import scala.concurrent.Future

object ProductCategoriesProxy extends JsonSupport {

  def retrieveCategory(id: Long, fetchProducts: Boolean = false): Future[ProductCategory] = {
    val uri: String = s"$CoreBaseUrl/api/product-categories/$id"

    Http().singleRequest(HttpRequest(uri = uri)
      .withHeaders(Authorization))
      .flatMap(response => {
        Unmarshal(response.entity).to[ProductCategoryResponse].flatMap(json =>
          json.toDomain
        )
      })
  }

}