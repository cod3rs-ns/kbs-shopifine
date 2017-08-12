package com.dmarjanovic.drools.external

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.dmarjanovic.drools.JsonSupport
import com.dmarjanovic.drools.domain.{BuyerCategory, ConsumptionThreshold}
import com.dmarjanovic.drools.hateoas.{BuyerCategoryResponse, ConsumptionThresholdCollectionResponse}

import scala.concurrent.Future

object BuyerCategoriesProxy extends JsonSupport {

  def retrieveBuyerCategory(id: Long): Future[BuyerCategory] = {
    val url: String = s"$CoreBaseUrl/api/buyer-categories/$id"

    Http().singleRequest(HttpRequest(uri = url)
      .withHeaders(Authorization))
      .flatMap(response => {
        Unmarshal(response.entity).to[BuyerCategoryResponse].flatMap(json =>
          json.toDomain
        )
      })
  }

  def retrieveThresholdsForBuyerCategory(id: Long): Future[Seq[ConsumptionThreshold]] = {
    val url: String = s"$CoreBaseUrl/api/buyer-categories/$id/thresholds"

    Http().singleRequest(HttpRequest(uri = url)
      .withHeaders(Authorization))
      .flatMap(response => {
        Unmarshal(response.entity).to[ConsumptionThresholdCollectionResponse].map(json =>
          json.data.map(_.toDomain)
        )
      })
  }

}
