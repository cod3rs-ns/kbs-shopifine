package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import hateoas.product_categories.{ProductCategoryCollectionResponse, ProductCategoryRequest, ProductCategoryResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.ProductCategoryRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductCategories @Inject()(productCategories: ProductCategoryRepository, secure: SecuredAuthenticator)
                                 (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles.SalesManager

  def create(): Action[JsValue] = secure.AuthWith(Seq(SalesManager)).async(parse.json) { implicit request =>
    request.body.validate[ProductCategoryRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec => {
        productCategories.save(spec.toDomain).map(category =>
          Created(Json.toJson(
            ProductCategoryResponse.fromDomain(category)
          ))
        )
      }
    )
  }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = Action.async { implicit request =>
    productCategories.retrieveAll(offset, limit).map(categories => {
      val self = routes.ProductCategories.retrieveAll(offset, limit).absoluteURL()
      val next = if (limit == categories.length) Some(routes.ProductCategories.retrieveAll(offset + limit, limit).absoluteURL()) else None

      Ok(Json.toJson(
        ProductCategoryCollectionResponse.fromDomain(categories, CollectionLinks(self, next))
      ))
    })
  }

  def update(id: Long): Action[JsValue] = secure.AuthWith(Seq(SalesManager)).async(parse.json) { implicit request =>
    request.body.validate[ProductCategoryRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec => {
        productCategories.modify(id, spec.toDomain).map(updated =>
          if (updated > 0) {
            Ok(Json.toJson(ProductCategoryResponse.fromDomain(spec.toDomain.copy(id = Some(id)))))
          }
          else {
            NotFound(Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Product Category $id doesn't exist!")))
            ))
          }
        )
      }
    )
  }

  def retrieveOne(id: Long): Action[AnyContent] = Action.async {
    productCategories.findOne(id).flatMap {
      case Some(category) => Future.successful(Ok(Json.toJson(ProductCategoryResponse.fromDomain(category))))

      case None => Future.successful(NotFound(Json.toJson(
        ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Product Category $id doesn't exist!")))
      )))
    }
  }

}
