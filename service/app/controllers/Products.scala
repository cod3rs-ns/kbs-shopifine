package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import external.DroolsProxy
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import products.{ProductCollectionResponse, ProductRequest, ProductResponse}
import services.ProductService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Products @Inject()(products: ProductService,
                         drools: DroolsProxy,
                         secure: SecuredAuthenticator)
                        (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles.Salesman

  def add(): Action[JsValue] = secure.AuthWith(Seq(Salesman)).async(parse.json) { implicit request =>
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
    val filters = request.queryString.filterKeys(_.startsWith("filter[")).map { case (k, v) =>
      k.substring(7, k.length - 1) -> v.mkString
    }

    products.retrieveProductsWithFilters(filters, offset, limit).map(products => {
      val prev = if (offset > 0) Some(routes.Products.retrieveAll(offset - limit, limit).absoluteURL()) else None
      val self = routes.Products.retrieveAll(offset, limit).absoluteURL()
      val next = if (limit == products.length) Some(routes.Products.retrieveAll(offset + limit, limit).absoluteURL()) else None

      Ok(Json.toJson(ProductCollectionResponse.fromDomain(products, CollectionLinks(prev, self, next))))
    })
  }

  def retrieveAllOutOfStock(offset: Int, limit: Int): Action[AnyContent] = Action.async {
    drools.productsOutOfStock.flatMap(response =>
      Future.sequence(response.data.map(data => products.retrieveOne(data.id).map(_.get))).map(outOfStockProducts => {
        // Update Product's 'fill stock' field
        outOfStockProducts.foreach(p => products.outOfStock(p.id.get))

        Ok(Json.toJson(ProductCollectionResponse.fromDomain(outOfStockProducts, CollectionLinks(self = "self"))))
      })
    )
  }

  def retrieveOne(id: Long): Action[AnyContent] = Action.async {
    products.retrieveOne(id) map {
      case Some(product) =>
        Ok(Json.toJson(ProductResponse.fromDomain(product)))

      case None =>
        NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Project $id doesn't exist!")))
        ))
    }
  }

  def fillStock(id: Long, quantity: Int): Action[AnyContent] = secure.AuthWith(Seq(Salesman)).async {
    products.fillStock(id, quantity).flatMap(updated => {
      if (updated > 0) {
        products.retrieveOne(id) map {
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
