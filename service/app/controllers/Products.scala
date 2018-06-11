package controllers

import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import db.MySqlWishlistItemRepository
import external.DroolsProxy
import javax.inject.Singleton
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import products.{ProductCollectionResponse, ProductRequest, ProductResponse, UpdateProductRequest}
import services.ProductService
import ws.NotificationPublisher

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Products @Inject()(products: ProductService,
                         drools: DroolsProxy,
                         secure: SecuredAuthenticator,
                         notificationPublisher: NotificationPublisher,
                         wishlists: MySqlWishlistItemRepository)(implicit val ec: ExecutionContext)
    extends Controller {

  import hateoas.JsonApi._
  import secure.Roles._

  def add(): Action[JsValue] = secure.AuthWith(Seq(Salesman)).async(parse.json) {
    implicit request =>
      request.body
        .validate[ProductRequest]
        .fold(
          _ =>
            Future.successful(BadRequest(Json.toJson(
              ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
            ))),
          spec =>
            products.save(spec.toDomain).map { product =>
              Created(
                Json.toJson(
                  ProductResponse.fromDomain(product)
                ))
          }
        )
  }

  def retrieveAllByUser(userId: Long, offset: Int, limit: Int): Action[AnyContent] =
    secure.AuthWith(Seq(Customer, Salesman, SalesManager)).async { implicit request =>
      if (request.user.isDefined && request.user.get.id.get != userId) {
        Future.successful(
          Forbidden(
            Json.toJson(
              ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
            )))
      } else {
        val filters = request.queryString.filterKeys(_.startsWith("filter[")).map {
          case (k, v) =>
            k.substring(7, k.length - 1) -> v.mkString
        }

        products.retrieveProductsWithFilters(filters, offset, limit).flatMap { products =>
          val prev =
            if (offset > 0) Some(routes.Products.retrieveAll(offset - limit, limit).absoluteURL())
            else None
          val self = routes.Products.retrieveAll(offset, limit).absoluteURL()
          val next =
            if (limit == products.length)
              Some(routes.Products.retrieveAll(offset + limit, limit).absoluteURL())
            else None

          Future
            .sequence {
              products.map { p =>
                wishlists.count(userId, p.id.get).map(c => p.copy(isInWishlist = c > 0))
              }
            }
            .map { ps =>
              Ok(Json.toJson(
                ProductCollectionResponse.fromDomain(ps, CollectionLinks(prev, self, next))))
            }

        }
      }
    }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = Action.async { implicit request =>
    val filters = request.queryString.filterKeys(_.startsWith("filter[")).map {
      case (k, v) =>
        k.substring(7, k.length - 1) -> v.mkString
    }

    products.retrieveProductsWithFilters(filters, offset, limit).map { products =>
      val prev =
        if (offset > 0) Some(routes.Products.retrieveAll(offset - limit, limit).absoluteURL())
        else None
      val self = routes.Products.retrieveAll(offset, limit).absoluteURL()
      val next =
        if (limit == products.length)
          Some(routes.Products.retrieveAll(offset + limit, limit).absoluteURL())
        else None

      Ok(
        Json.toJson(
          ProductCollectionResponse.fromDomain(products, CollectionLinks(prev, self, next))))
    }
  }

  def retrieveAllOutOfStock(offset: Int, limit: Int): Action[AnyContent] = Action.async {
    drools.productsOutOfStock.flatMap(response =>
      Future.sequence(response.data.map(data => products.retrieveOne(data.id).map(_.get))).map {
        outOfStockProducts =>
          // Update Product's 'fill stock' field
          outOfStockProducts.foreach(p => products.outOfStock(p.id.get))

          Ok(
            Json.toJson(ProductCollectionResponse.fromDomain(outOfStockProducts,
                                                             CollectionLinks(self = "self"))))
    })
  }

  def retrieveOneByUser(userId: Long, id: Long): Action[AnyContent] =
    secure.AuthWith(Seq(Customer, Salesman, SalesManager)).async { implicit request =>
      if (request.user.isDefined && request.user.get.id.get != userId) {
        Future.successful(
          Forbidden(
            Json.toJson(
              ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
            )))
      } else {
        products.retrieveOne(id).flatMap {
          case Some(product) =>
            wishlists
              .count(userId, product.id.get)
              .map(count => product.copy(isInWishlist = count > 0))
              .map(p => Ok(Json.toJson(ProductResponse.fromDomain(p))))
          case None =>
            Future.successful(NotFound(Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Product $id doesn't exist!")))
            )))
        }
      }
    }

  def retrieveOne(id: Long): Action[AnyContent] = Action.async {
    products.retrieveOne(id) map {
      case Some(product) =>
        Ok(Json.toJson(ProductResponse.fromDomain(product)))

      case None =>
        NotFound(
          Json.toJson(
            ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Product $id doesn't exist!")))
          ))
    }
  }

  def fillStock(id: Long, quantity: Int): Action[AnyContent] =
    secure.AuthWith(Seq(Salesman)).async {
      products.fillStock(id, quantity).flatMap {
        case None =>
          Future.successful(
            NotFound(Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Product $id doesn't exist!")))
            )))
        case Some(_) =>
          products.retrieveOne(id) map {
            case Some(product) => Ok(Json.toJson(ProductResponse.fromDomain(product)))
            case None =>
              NotFound(
                Json.toJson(
                  ErrorResponse(
                    errors = Seq(Error(NOT_FOUND.toString, s"Product $id doesn't exist!")))
                ))
          }
      }
    }

  def updatePrice(): Action[JsValue] =
    secure.AuthWith(Seq(Salesman)).async(parse.json) { implicit request =>
      request.body
        .validate[UpdateProductRequest]
        .fold(
          _ =>
            Future.successful(BadRequest(Json.toJson(
              ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
            ))),
          spec => {
            val id    = spec.data.id
            val price = spec.data.attributes.price
            products.retrieveOne(id).flatMap {
              case Some(p) =>
                products.updatePrice(spec.data.id, price).map { _ =>
                  val updated = p.copy(price = price)
                  notificationPublisher.productPriceChanged(updated)
                  Created(Json.toJson(ProductResponse.fromDomain(updated)))
                }
              case None =>
                Future.successful(
                  NotFound(
                    Json.toJson(
                      ErrorResponse(errors =
                        Seq(Error(NOT_FOUND.toString, s"Product ${spec.data.id} doesn't exist!")))
                    )))
            }
          }
        )
    }
}
