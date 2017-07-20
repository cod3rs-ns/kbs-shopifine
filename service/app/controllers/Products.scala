package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
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
      failures => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec => {
        products.save(spec.toDomain).map({ product =>
          Created(Json.toJson(
            ProductResponse.fromDomain(product)
          ))
        })
      }
    )
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
      case Some(product) =>
        Ok(Json.toJson(ProductResponse.fromDomain(product)))

      case None =>
        NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Project $id doesn't exist!")))
        ))
    }
  }

  def fillStock(id: Long, quantity: Int): Action[AnyContent] = Action.async {
    products.fillStock(id, quantity).flatMap(updated => {
      if (updated > 0) {
        products.retrieve(id) map {
          case Some(product) =>
            Ok(Json.toJson(ProductResponse.fromDomain(product)))

          case None =>
            NotFound(Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Project $id doesn't exist!")))
            ))
        }
      }
      else
        Future.successful(NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Project $id doesn't exist!")))
        )))
    })
  }

}
