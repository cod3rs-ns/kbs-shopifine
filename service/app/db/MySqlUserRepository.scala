package db

import javax.inject.Inject

import domain.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repositories.UserRepository
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.Future

class MySqlUserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends UserRepository with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {
  override def save(user: User): Future[User] = {
    val items = users returning users.map(_.id) into ((item, id) => item.copy(id = Some(id)))
    db.run(items += user)
  }
}
