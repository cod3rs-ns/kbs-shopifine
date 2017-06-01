package db

import java.sql.Timestamp

import domain.{BuyerCategory, User, UserRole}
import org.joda.time.DateTime
import slick.ast.BaseTypedType
import slick.lifted.{ForeignKeyQuery, ProvenShape, TableQuery}
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcType

trait DatabaseSchema {
  val buyerCategories: TableQuery[BuyerCategories] = TableQuery[BuyerCategories]
  val users: TableQuery[Users] = TableQuery[Users]

  implicit val dateTimeMapper: JdbcType[DateTime] with BaseTypedType[DateTime] = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime)
  )

  implicit val userRoleMapping: JdbcType[UserRole] with BaseTypedType[UserRole] = MappedColumnType.base[UserRole, String](_.name, UserRole.valueOf)

  class BuyerCategories(tag: Tag) extends Table[BuyerCategory](tag, "buyer_categories") {
    def * : ProvenShape[BuyerCategory] = {
      val props = (id.?, name)

      props <> (BuyerCategory.tupled, BuyerCategory.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")
  }

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def * : ProvenShape[User] = {
      val props = (id.?, username, password, firstName, lastName, role, address, buyerCategoryId, points, registeredAt)

      props <> (User.tupled, User.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def password: Rep[String] = column[String]("password")

    def firstName: Rep[String] = column[String]("first_name")

    def lastName: Rep[String] = column[String]("last_name")

    def role: Rep[UserRole] = column[UserRole]("role")

    def address: Rep[Option[String]] = column[Option[String]]("address")

    def buyerCategoryId: Rep[Option[Long]] = column[Option[Long]]("buyer_category")

    def points: Rep[Option[Long]] = column[Option[Long]]("points")

    def registeredAt: Rep[DateTime] = column[DateTime]("registered_at")

    def buyerCategory: ForeignKeyQuery[BuyerCategories, BuyerCategory] = foreignKey("buyer_category_fk", buyerCategoryId, buyerCategories)(_.id.?)
  }

}
