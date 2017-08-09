package com.dmarjanovic.drools

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.dmarjanovic.drools.domain.Product
import com.dmarjanovic.drools.hateoas.{BillItemRequest, CollectionLinks, ProductCollectionResponse, ProductCollectionResponseJson}
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory

import scala.concurrent.{Future, blocking}

object DroolsService extends JsonSupport {

  val ProductsUrl: String = "http://localhost:9000/api/products"

  private val log = LoggerFactory.getLogger("Drools Service")

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("drools-service-system")
    implicit val materializer = ActorMaterializer()

    implicit val executionContext = system.dispatcher

    var products: List[Product] = List()

    def retrieveProductsFrom(url: String): Future[_] = {
      val map = Http().singleRequest(HttpRequest(uri = url)).map(response => {
        log.info(s"Sent request to $url")
        Unmarshal(response.entity).to[ProductCollectionResponse].map(productsJson => {
          products = products ++ productsJson.data.map(p => p.toDomain)
          val next = productsJson.links.next
          if (next isDefined) {
            //            Future {
            //              blocking {
            retrieveProductsFrom(next.get)
            //              }
            //            }
          }
        })
      })
      map
    }

    val route =
      pathPrefix("api") {
        path("products") {
          put {
            complete {
              retrieveProductsFrom(ProductsUrl).map(_ => {
                                RulesEngine.determineProductWeNeedToFillStock(products)
                                ProductCollectionResponseJson.fromDomain(products, links = CollectionLinks(self = "self"))
              })

//              Future {
//                blocking {
//                  retrieveProductsFrom(ProductsUrl)
//                }
//              }.map(_ => {
//                RulesEngine.determineProductWeNeedToFillStock(products)
//                ProductCollectionResponseJson.fromDomain(products, links = CollectionLinks(self = "self"))
//              })
            }
          }
        }

        path("bill-item") {
          put {
            entity(as[BillItemRequest]) { spec =>
              spec.toDomain.map(item => {
                RulesEngine.calculateBillItemDiscounts(item)
              })
              complete("test")
            }
          }
        }
      }

    val config: Config = ConfigFactory.load()
    val host: String = config.getString("http.host")
    val port: Int = config.getInt("http.port")

    Http().bindAndHandle(handler = route, interface = host, port = port)

  }
}