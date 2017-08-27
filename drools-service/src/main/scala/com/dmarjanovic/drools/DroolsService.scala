package com.dmarjanovic.drools

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.dmarjanovic.drools.external.{BillsProxy, ProductsProxy}
import com.dmarjanovic.drools.hateoas._
import com.typesafe.config.{Config, ConfigFactory}

object DroolsService extends JsonSupport {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("drools-service-system")
    implicit val materializer = ActorMaterializer()

    implicit val executionContext = system.dispatcher

    val routes =
      pathPrefix("api") {
        path("products") {
          get {
            complete {
              ProductsProxy.retrieveProducts().map(products => {
                RulesEngine.determineProductWeNeedToFillStock(products.toList)
                ProductCollectionResponseJson.fromDomain(products.filter(_.fillStock), links = CollectionLinks(self = "self"))
              })
            }
          }
        } ~
          pathPrefix("users") {
            path(IntNumber / "bill-items" / "discounts") {
              userId => {
                put {
                  entity(as[BillItemRequest]) { spec =>
                    complete {
                      spec.toDomain(userId).map(item => {
                        RulesEngine.calculateBillItemDiscounts(item)
                        BillItemWithDiscountsResponseJson.fromDomain(item)
                      })
                    }
                  }
                }
              }
            }
          } ~
          pathPrefix("users") {
            path(IntNumber / "bills" / IntNumber / "discounts") {
              (userId, billId) => {
                get {
                  complete {
                    BillsProxy.retrieveBill(userId, billId).map(bill => {
                      RulesEngine.calculateBillDiscounts(bill)
                      BillWithDiscountsResponseJson.fromDomain(bill)
                    })
                  }
                }
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
