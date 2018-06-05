package db

import javax.inject.Inject

import akka.NotUsed
import akka.stream.scaladsl.Source
import domain.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.UserRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.{ExecutionContext, Future}

class MySqlUserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ex: ExecutionContext)
    extends UserRepository
    with HasDatabaseConfigProvider[JdbcProfile]
    with DatabaseSchema {

  override def save(user: User): Future[User] = {
    val items = users returning users.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += user)
  }

  override def update(user: User): Future[User] = {
    val action = users.filter(_.id === user.id.get).update(user).map {
      case 0 => throw new IllegalArgumentException(s"User with id $user.id.get does not exists.")
      case _ => user
    }
    db.run(action)
  }

  override def retrieve(id: Long): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  override def updateUserPoints(id: Long, points: Long): Future[Int] = {
    val query =
      sql"""
           UPDATE
              users
           SET
              points = points + $points
           WHERE id = $id;
        """.as[Int].head
    db.run(query)
  }

  override def findByUsernameAndPassword(username: String, password: String): Future[Option[User]] =
    db.run(users.filter(u => u.username === username && u.password === password).result.headOption)

  override def findByUsername(username: String): Future[Option[User]] =
    db.run(users.filter(_.username === username).result.headOption)

  override def findByGoogleId(id: String): Future[Option[User]] =
    db.run(users.filter(_.googleAccountId === id).result.headOption)

  def getUserIds: Source[Long, NotUsed] = {
    val action = (for (u <- users) yield u.id).result

    val databasePublisher = db stream action

    Source fromPublisher databasePublisher
  }
}
