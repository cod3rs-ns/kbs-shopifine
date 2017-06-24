package controllers

import commons.CollectionLinks
import domain.ConsumptionThreshold
import hateoas.buyer_categories._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.Future

class BuyerCategories extends Controller {

  import hateoas.JsonApi._

  def create(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[BuyerCategoryRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson("Malformed JSON specified."))),

      spec => Future.successful(
        Ok(Json.toJson(
          BuyerCategoryResponse.fromDomain(spec.toDomain.copy(id = Some(1))))
        ))
    )
  }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = Action.async {
    Future.successful(
      Ok(Json.toJson(
        BuyerCategoryCollectionResponse.fromDomain(Seq(), CollectionLinks("self", Some("next")))
      ))
    )
  }

  def retrieveThresholds(buyerCategoryId: Long, offset: Int, limit: Int): Action[AnyContent] = Action.async {
    val thresholds = Seq(
      ConsumptionThreshold(id = Some(1), from = 1, to = 10, award = 20.25),
      ConsumptionThreshold(id = Some(2), from = 11, to = 39, award = 40.0)
    )

    Future.successful(
      Ok(Json.toJson(
        ConsumptionThresholdCollectionResponse.fromDomain(thresholds, CollectionLinks("self", Some("next")))
      ))
    )
  }

  def addThreshold(id: Long): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[ConsumptionThresholdRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson("Malformed JSON specified."))),

      spec => Future.successful(
        Ok(Json.toJson(
          ConsumptionThresholdResponse.fromDomain(spec.toDomain.copy(id = Some(1))))
        ))
    )
  }

  def removeThreshold(categoryId: Long, thresholdId: Long): Action[AnyContent] = Action.async {
    // Bad Request  If category doesn't exist
    // Forbidden    If no privileges
    // Not Found    If threshold doesn't exist
    // No Content   Successfully deleted
    Future.successful(NoContent)
  }

}
