package controllers

import commons.CollectionLinks
import hateoas.buyer_categories.{BuyerCategoryCollectionResponse, BuyerCategoryRequest, BuyerCategoryResponse}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

class BuyerCategories extends Controller {

  import hateoas.JsonApi._

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[BuyerCategoryRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson("Malformed JSON specified."))),

      spec => Future.successful(Ok(Json.toJson(BuyerCategoryResponse.fromDomain(spec.toDomain.copy(id = Some(1))))))
    )
  }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = Action.async {
    Future.successful(
      Ok(Json.toJson(
        BuyerCategoryCollectionResponse.fromDomain(Seq(), CollectionLinks("self", Some("next")))
      ))
    )
  }

  def retrieveThresholds(id: Long, offset: Int, limit: Int): Action[AnyContent] = Action.async {
    Future.successful(Ok("ok"))
  }

  def addThreshold(id: Long): Action[AnyContent] = Action.async {
    Future.successful(Ok("ok"))
  }

  def removeThreshold(categoryId: Long, thresholdId: Long): Action[AnyContent] = Action.async {
    Future.successful(Ok("ok"))
  }

}
