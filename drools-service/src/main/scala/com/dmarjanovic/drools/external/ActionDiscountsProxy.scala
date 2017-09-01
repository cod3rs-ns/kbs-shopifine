package com.dmarjanovic.drools.external

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dmarjanovic.drools.JsonSupport
import com.dmarjanovic.drools.domain.ActionDiscount
import com.dmarjanovic.drools.hateoas.ActionDiscountCollectionResponse

import scala.concurrent.Future

object ActionDiscountsProxy extends JsonSupport {

  def retrieveActionDiscountsFor(productCategory: Long): Future[Seq[ActionDiscount]] = {
    val uri: String = s"$CoreBaseUrl/api/product-categories/$productCategory/action-discounts"

    Http().singleRequest(HttpRequest(uri = uri)
      .withHeaders(Authorization))
      .flatMap(response => {
        Unmarshal(response.entity).to[ActionDiscountCollectionResponse].map(json => json.toDomain)
      })
  }

}
