package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import hateoas.action_discounts.{ActionDiscountCollectionResponse, ActionDiscountRequest, ActionDiscountResponse}
import org.joda.time.DateTime
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.{ActionDiscountRepository, ProductCategoryRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ActionDiscounts @Inject()(discounts: ActionDiscountRepository,
                                categories: ProductCategoryRepository,
                                secure: SecuredAuthenticator)
                               (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles.SalesManager

  def create: Action[JsValue] = secure.AuthWith(Seq(SalesManager)).async(parse.json) { implicit request =>
    request.body.validate[ActionDiscountRequest].fold(
      _ => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec => discounts.save(spec.toDomain).map(discount =>
        Created(Json.toJson(
          ActionDiscountResponse.fromDomain(discount)
        ))
      )
    )
  }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(SalesManager)).async { implicit request =>
    discounts.retrieveAll(offset, limit).map(discounts => {
      val self = routes.ActionDiscounts.retrieveAll(offset, limit).absoluteURL()
      val next = if (limit == discounts.length) Some(routes.ActionDiscounts.retrieveAll(offset + limit, limit).absoluteURL()) else None

      Ok(Json.toJson(
        ActionDiscountCollectionResponse.fromDomain(discounts, CollectionLinks(self = self, next = next))
      ))
    })
  }

  def update(id: Long): Action[JsValue] = secure.AuthWith(Seq(SalesManager)).async(parse.json) { implicit request =>
    request.body.validate[ActionDiscountRequest].fold(
      _ => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec => discounts.modify(id, spec.toDomain).map(updated =>
        if (updated > 0) {
          Ok(Json.toJson(ActionDiscountResponse.fromDomain(spec.toDomain.copy(id = Some(id)))))
        }
        else {
          NotFound(Json.toJson(
            ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Action Discount $id doesn't exist!")))
          ))
        }
      )
    )
  }

  def retrieveAllByCategory(id: Long, date: String, offset: Int, limit: Int): Action[AnyContent] = Action.async { implicit request =>
    categories.findOne(id).flatMap {
      case Some(_) => discounts.retrieveByProductCategory(id).map(result => {

        val parsed: Option[DateTime] = if ("" != date) Some(DateTime.parse(date)) else None
        val discounts = (for ((discount, _) <- result) yield discount).filter(d => isBetween(parsed, d.from, d.to))

        val self = routes.ActionDiscounts.retrieveAllByCategory(id, date, offset, limit).absoluteURL()
        val next = if (limit == discounts.length) Some(routes.ActionDiscounts.retrieveAllByCategory(id, date, offset + limit, limit).absoluteURL()) else None

        Ok(Json.toJson(
          ActionDiscountCollectionResponse.fromDomain(discounts, CollectionLinks(self = self, next = next))
        ))
      })

      case None => Future.successful(
        NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Product category $id doesn't exist!")))
        )))
    }
  }

  def addProductCategory(id: Long, categoryId: Long): Action[AnyContent] = secure.AuthWith(Seq(SalesManager)).async { implicit request =>
    discounts.findOne(id).flatMap {
      case Some(_) =>
        categories.findOne(categoryId).flatMap {
          case Some(_) =>
            discounts.addProductCategory(id, categoryId).flatMap(result =>
              discounts.findOne(result.discount).map(discount =>
                Ok(Json.toJson(ActionDiscountResponse.fromDomain(discount.get))))
            )

          case None =>
            Future.successful(NotFound(Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"ProductCategory $categoryId doesn't exist!")))
            )))
        }

      case None =>
        Future.successful(NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Action Discount $id doesn't exist!")))
        )))
    }
  }

  def removeProductCategory(id: Long, categoryId: Long): Action[AnyContent] = secure.AuthWith(Seq(SalesManager)).async { implicit request =>
    discounts.findOne(id).flatMap {
      case Some(_) =>
        categories.findOne(categoryId).flatMap {
          case Some(_) =>
            discounts.removeProductCategory(id, categoryId).map(affected =>
              if (affected > 0)
                NoContent
              else
                NotFound(Json.toJson(
                  ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"ProductCategory $categoryId doesn't exist!")))
                ))
            )

          case None =>
            Future.successful(NotFound(Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"ProductCategory $categoryId doesn't exist!")))
            )))
        }

      case None =>
        Future.successful(NotFound(Json.toJson(
          ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Action Discount $id doesn't exist!")))
        )))
    }
  }

  private def isBetween(date: Option[DateTime], from: DateTime, to: DateTime): Boolean =
    date.fold(true)(d => d.isAfter(from) && d.isBefore(to))

}
