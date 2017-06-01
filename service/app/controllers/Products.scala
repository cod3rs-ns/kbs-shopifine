package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class Products @Inject() extends Controller {

  def add(): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson("Added!")))
  }

  def modify(id: Long): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(id)))
  }

  def retrieveAll(limit: Int, offset: Int): Action[AnyContent] = Action.async {
    implicit request => {
      Future.successful(Ok("ok"))
    }
  }

  def retrieveOne(id: Long): Action[AnyContent] = Action.async {
    Future.successful(Ok("ok"))
  }

  def fillStock(id: Long, quantity: Int): Action[AnyContent] = Action.async {
    Future.successful(Ok(Json.toJson(id)))
  }

}
