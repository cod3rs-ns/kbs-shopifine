package controllers

import javax.inject.Singleton

import bills.{BillCollectionResponse, BillRequest, BillResponse}
import com.google.inject.Inject
import commons.CollectionLinks
import domain.BillState
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, Controller}
import repositories.{BillItemRepository, BillRepository}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Bills @Inject()(bills: BillRepository, billItems: BillItemRepository)
                     (implicit val ec: ExecutionContext) extends Controller {

  import hateoas.JsonApi._

  def create(userId: Long): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[BillRequest].fold(
      failures => Future.successful(BadRequest(Json.toJson("Malformed JSON specified."))),

      spec => {
        bills.save(spec.toDomain).map({
          bill => Created(Json.toJson(BillResponse.fromDomain(bill)))
        })
      }
    )
  }

  def retrieveAllByUser(userId: Long, offset: Int, limit: Int): Action[AnyContent] = Action.async { implicit request =>
    bills.retrieveByUser(userId, offset, limit).map({ result =>
      val self = routes.Bills.retrieveAllByUser(userId, offset, limit).absoluteURL()
      val next = if (result.size == limit) Some(routes.Bills.retrieveAllByUser(userId, offset, limit).absoluteURL()) else None

      Ok(Json.toJson(
        BillCollectionResponse.fromDomain(result, CollectionLinks(self, next))
      ))
    })
  }

  def retrieveAll(offset: Int, limit: Int): Action[AnyContent] = Action.async { implicit request =>
    bills.retrieveAll(offset, limit).map({ result =>
      val self = routes.Bills.retrieveAll(offset, limit).absoluteURL()
      val next = if (result.size == limit) Some(routes.Bills.retrieveAll(offset, limit).absoluteURL()) else None

      Ok(Json.toJson(
        BillCollectionResponse.fromDomain(result, CollectionLinks(self, next))
      ))
    })
  }

  def changeState(id: Long, state: String): Action[AnyContent] = Action.async {
    // FIXME Handle error
    val toState = BillState.valueOf(state.toUpperCase)

    bills.setState(id, toState).flatMap(
      affected => {
        // FIXME Handle Not Found
        if (affected > 0) {
          bills.retrieveOne(id).map({ bill =>
            Ok(Json.toJson(BillResponse.fromDomain(bill.get)))
          })
        }
        else {
          Future.successful(NotFound("test-message"))
        }
      }
    )
  }

}
