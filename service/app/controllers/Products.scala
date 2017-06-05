package controllers

import javax.inject.Singleton

import domain.Product
import com.google.inject.Inject
import commons.CollectionLinks
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import products.{ProductCollectionResponse, ProductRequest, ProductResponse}
import repositories.ProductRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Products @Inject()(products: ProductRepository)(implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._

  def add(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ProductRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson("Malformed JSON provided."))),

      spec => {
        products.save(spec.toDomain).map({ product =>
          Ok(Json.toJson(ProductResponse.fromDomain(product)))
        })
      }
    )
  }

  def modify(id: Long): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(id)))
  }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = Action.async { implicit request =>
    products.retrieveAll(offset, limit).map(products => {
      val self = routes.Products.retrieveAll(offset, limit).absoluteURL()
      val next = if (limit == products.length) Some(routes.Products.retrieveAll(offset + limit, limit).absoluteURL()) else None

      Ok(Json.toJson(ProductCollectionResponse.fromDomain(products, CollectionLinks(self, next))))
    })
  }

  def retrieveOne(id: Long): Action[AnyContent] = Action.async {
    products.retrieve(id) map {
      case Some(product) => Ok(Json.toJson(ProductResponse.fromDomain(product)))
      case None => NotFound(s"Project with $id doesn't exist!")
    }
  }

  def fillStock(id: Long, quantity: Int): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(id)))
  }

}
