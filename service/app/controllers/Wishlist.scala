package controllers

import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import javax.inject.Singleton
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.{ProductRepository, WishlistItemsRepository}
import wishlist.{WishlistItemCollectionResponse, WishlistItemRequest, WishlistItemResponse}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Wishlist @Inject()(wishlists: WishlistItemsRepository,
                         products: ProductRepository,
                         secure: SecuredAuthenticator)(implicit val ec: ExecutionContext)
    extends Controller {

  import hateoas.JsonApi._
  import secure.Roles._

  def retrieve(userId: Long): Action[AnyContent] = secure.AuthWith(Seq(Customer)).async {
    implicit request =>
      if (request.user.isDefined && request.user.get.id.get != userId) {
        Future.successful(
          Forbidden(
            Json.toJson(
              ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
            )))
      } else {
        wishlists.retrieveAll(userId).flatMap { wi =>
          Future.successful(
            Ok(
              Json.toJson(WishlistItemCollectionResponse.fromDomain(
                wi,
                CollectionLinks(self = routes.Wishlist.retrieve(userId).absoluteURL())))))
        }

      }
  }

  def removeItem(userId: Long, id: Long): Action[AnyContent] =
    secure.AuthWith(Seq(Customer)).async { implicit request =>
      wishlists.retrieve(id).flatMap {
        case Some(_) =>
          wishlists.delete(id).map(_ => NoContent)

        case None =>
          Future.successful(
            NotFound(
              Json.toJson(
                ErrorResponse(
                  errors = Seq(Error(NOT_FOUND.toString, s"Wishlist Item $id doesn't exist!")))
              )))
      }
    }

  def removeProduct(userId: Long, productId: Long): Action[AnyContent] =
    secure.AuthWith(Seq(Customer)).async { implicit request =>
      wishlists.retrieveByProduct(productId).flatMap {
        case Some(_) =>
          wishlists.deleteProduct(productId).map(_ => NoContent)

        case None =>
          Future.successful(
            NotFound(
              Json.toJson(
                ErrorResponse(
                  errors = Seq(Error(NOT_FOUND.toString, s"Product $productId is not part of Wishlist!")))
              )))
      }
    }

  def addProduct(userId: Long): Action[JsValue] = secure.AuthWith(Seq(Customer)).async(parse.json) {
    implicit request =>
      if (request.user.isDefined && request.user.get.id.get != userId) {
        Future.successful(
          Forbidden(
            Json.toJson(
              ErrorResponse(errors = Seq(Error(FORBIDDEN.toString, "No privileges.")))
            )))
      } else {
        request.body
          .validate[WishlistItemRequest]
          .fold(
            _ =>
              Future.successful(
                BadRequest(
                  Json.toJson(
                    ErrorResponse(
                      errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
                  ))),
            spec => {
              val item = spec.toDomain
              products.retrieve(item.productId).flatMap {
                case Some(_) =>
                  wishlists.count(userId, item.productId).flatMap { count =>
                    if (count > 0) {
                      Future.successful(
                        BadRequest(
                          Json.toJson(
                            ErrorResponse(errors =
                              Seq(Error(BAD_REQUEST.toString, "Product already on wishlist.")))
                          )))
                    } else {
                      wishlists.save(spec.toDomain).map { wi =>
                        Created(Json.toJson(
                          WishlistItemResponse.fromDomain(wi)
                        ))
                      }
                    }
                  }

                case None =>
                  Future.successful(
                    NotFound(Json.toJson(
                      ErrorResponse(errors =
                        Seq(Error(NOT_FOUND.toString, s"Product ${item.productId} doesn't exist!")))
                    )))
              }
            }
          )
      }
  }

}
