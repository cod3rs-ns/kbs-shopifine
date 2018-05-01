package db

import javax.inject.Inject

import domain.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.UserRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

class MySqlUserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {

  override def save(user: User): Future[User] = {
    val items = users returning users.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += user)
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

}
