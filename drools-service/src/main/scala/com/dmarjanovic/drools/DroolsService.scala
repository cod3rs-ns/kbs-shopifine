package com.dmarjanovic.drools

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.dmarjanovic.drools.external.ProductsProxy
import com.dmarjanovic.drools.hateoas.{BillItemRequest, CollectionLinks, ProductCollectionResponseJson}
import com.typesafe.config.{Config, ConfigFactory}

object DroolsService extends JsonSupport {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("drools-service-system")
    implicit val materializer = ActorMaterializer()

    implicit val executionContext = system.dispatcher

    val routes =
      pathPrefix("api") {
        path("products") {
          put {
            complete {
              ProductsProxy.retrieveProducts.map(products => {
                RulesEngine.determineProductWeNeedToFillStock(products.toList)
                ProductCollectionResponseJson.fromDomain(products, links = CollectionLinks(self = "self"))
              })
            }
          }
        } ~
          pathPrefix("bill-items") {
            path("discounts") {
              put {
                entity(as[BillItemRequest]) { spec =>
                  spec.toDomain.map(item => {
                    RulesEngine.calculateBillItemDiscounts(item)
                  })
                  complete("Should handle Bill Item discounts and Price")
                }
              }
            }
          } ~
            pathPrefix("bills") {
              path("discounts") {
                put {
                  complete("Should handle Bill discounts and Price")
                  }
                }
              }
      }

    val config: Config = ConfigFactory.load()
    val host: String = config.getString("http.host")
    val port: Int = config.getInt("http.port")

    Http().bindAndHandle(handler = routes, interface = host, port = port)

  }
}