package controllers

import javax.inject._

import domain.{Product, ProductCategory}
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import products.{ProductCollectionResponse, ProductResponse}

import scala.concurrent.Future

@Singleton
class Products @Inject() extends Controller {
  import hateoas.JsonApi._

  def add(): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson("Added!")))
  }

  def modify(id: Long): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(id)))
  }

  def retrieveAll(`page[limit]`: Int, `page[offset]`: Int): Action[AnyContent] = Action.async {
    implicit request => {

      val limit = `page[limit]`
      val offset = `page[offset]`

      val category = ProductCategory(
        id = Some(1),
        name = "Product Category",
        maximumDiscount = 42.0
      )

      val p1 = Product(
        id = Some(1),
        name = "Product 1",
        category = category,
        price = 11,
        quantity = 22,
        createdAt = DateTime.now,
        minQuantity = 0
      )

      val p2 = Product(
        id = Some(2),
        name = "Product 2",
        category = category,
        price = 11,
        quantity = 22,
        createdAt = DateTime.now,
        minQuantity = 0
      )

      Future.successful(Ok(Json.toJson(ProductCollectionResponse.fromDomain(Seq(p1, p2)))))
    }
  }

  def retrieveOne(id: Long): Action[AnyContent] = Action.async {
    val category = ProductCategory(
      id = Some(1),
      name = "Product Category",
      maximumDiscount = 42.0
    )

    val product = Product(
      id = Some(1),
      name = "Product 1",
      category = category,
      price = 11,
      quantity = 22,
      createdAt = DateTime.now,
      minQuantity = 0
    )

    Future.successful(Ok(Json.toJson(ProductResponse.fromDomain(product))))
  }

  def fillStock(id: Long, quantity: Int): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(id)))
  }

}
