package db

import java.sql.Timestamp

import domain.{User, UserRole}
import org.joda.time.DateTime
import slick.ast.BaseTypedType
import slick.lifted.{ProvenShape, TableQuery}
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcType

trait DatabaseSchema {
  val users: TableQuery[Users] = TableQuery[Users]

  implicit val dateTimeMapper: JdbcType[DateTime] with BaseTypedType[DateTime] = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime)
  )

  implicit val userRoleMapping: JdbcType[UserRole] with BaseTypedType[UserRole] = MappedColumnType.base[UserRole, String](_.name, UserRole.valueOf)

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def * : ProvenShape[User] = {
      val props = (id.?, username, password, firstName, lastName, role, registeredAt)

      props <> (User.tupled, User.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def password: Rep[String] = column[String]("password")

    def firstName: Rep[String] = column[String]("first_name")

    def lastName: Rep[String] = column[String]("last_name")

    def role: Rep[UserRole] = column[UserRole]("role")

    def registeredAt: Rep[DateTime] = column[DateTime]("registered_at")
  }

}
