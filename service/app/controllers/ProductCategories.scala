package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext

@Singleton
class ProductCategories @Inject()()
                                 (implicit val ec: ExecutionContext) extends Controller {

  def create() = ???

  def retrieveAll(offset: Int, limit: Int) = ???

  def update(id: Long) = ???

  def retrieveOne(id: Long) = ???

}
