package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext

@Singleton
class Bills @Inject()()(implicit val ec: ExecutionContext) extends Controller {

  def create(userId: Long) = ???

  def retrieveAllByUser(userId: Long, offset: Int, limit: Int) = ???

  def retrieveAll(offset: Int, limit: Int) = ???

  def changeState(id: Long, state: String) = ???

}
