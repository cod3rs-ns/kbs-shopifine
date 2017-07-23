package repositories

import domain.User

import scala.concurrent.Future

trait UserRepository {

  def save(user: User): Future[User]

  def retrieve(id: Long): Future[Option[User]]

  def findByUsernameAndPassword(username: String, password: String): Future[Option[User]]

  def findByUsername(username: String): Future[Option[User]]

}
