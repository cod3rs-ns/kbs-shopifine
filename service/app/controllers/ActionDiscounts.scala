package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import commons.{CollectionLinks, Error, ErrorResponse}
import hateoas.action_discounts.{ActionDiscountCollectionResponse, ActionDiscountRequest, ActionDiscountResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.ActionDiscountRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ActionDiscounts @Inject()(actionDiscounts: ActionDiscountRepository, secure: SecuredAuthenticator)
                               (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._
  import secure.Roles.SalesManager

  def create: Action[JsValue] = secure.AuthWith(Seq(SalesManager)).async(parse.json) { implicit request =>
    request.body.validate[ActionDiscountRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec => {
        actionDiscounts.save(spec.toDomain).map(discount =>
          Created(Json.toJson(
            ActionDiscountResponse.fromDomain(discount)
          ))
        )
      }
    )
  }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = secure.AuthWith(Seq(SalesManager)).async { implicit request =>
    actionDiscounts.retrieveAll(offset, limit).map(discounts => {
      val self = routes.ActionDiscounts.retrieveAll(offset, limit).absoluteURL()
      val next = if (limit == discounts.length) Some(routes.ActionDiscounts.retrieveAll(offset + limit, limit).absoluteURL()) else None

      Ok(Json.toJson(
        ActionDiscountCollectionResponse.fromDomain(discounts, CollectionLinks(self, next))
      ))
    })
  }


  def update(id: Long): Action[JsValue] = secure.AuthWith(Seq(SalesManager)).async(parse.json) { implicit request =>
    request.body.validate[ActionDiscountRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson(
        ErrorResponse(errors = Seq(Error(BAD_REQUEST.toString, "Malformed JSON specified.")))
      ))),

      spec => {
        actionDiscounts.modify(id, spec.toDomain).map(updated =>
          if (updated > 0) {
            Ok(Json.toJson(ActionDiscountResponse.fromDomain(spec.toDomain.copy(id = Some(id)))))
          }
          else {
            NotFound(Json.toJson(
              ErrorResponse(errors = Seq(Error(NOT_FOUND.toString, s"Action Discount $id doesn't exist!")))
            ))
          }
        )
      }
    )
  }

}
