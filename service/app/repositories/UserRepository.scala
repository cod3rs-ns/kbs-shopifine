package repositories

import akka.NotUsed
import akka.stream.scaladsl.Source
import domain.User

import scala.concurrent.Future

trait UserRepository {

  def save(user: User): Future[User]

  def update(user: User): Future[User]

  def retrieve(id: Long): Future[Option[User]]

  def updateUserPoints(id: Long, points: Long): Future[Int]

  def findByUsernameAndPassword(username: String, password: String): Future[Option[User]]

  def findByUsername(username: String): Future[Option[User]]

  def findByGoogleId(id: String): Future[Option[User]]

  def getUserIds: Source[Long, NotUsed]

}
