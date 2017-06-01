package db

import java.sql.Timestamp

import domain._
import org.joda.time.DateTime
import slick.ast.BaseTypedType
import slick.lifted.{ForeignKeyQuery, ProvenShape, TableQuery}
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcType

trait DatabaseSchema {
  val buyerCategories: TableQuery[BuyerCategories] = TableQuery[BuyerCategories]
  val users: TableQuery[Users] = TableQuery[Users]

  val productCategories: TableQuery[ProductCategories] = TableQuery[ProductCategories]
  val products: TableQuery[Products] = TableQuery[Products]

  val bills: TableQuery[Bills] = TableQuery[Bills]
  val billItems: TableQuery[BillItems] = TableQuery[BillItems]
  val billDiscounts: TableQuery[BillDiscounts] = TableQuery[BillDiscounts]
  val itemDiscounts: TableQuery[ItemDiscounts] = TableQuery[ItemDiscounts]

  implicit val dateTimeMapper: JdbcType[DateTime] with BaseTypedType[DateTime] = MappedColumnType.base[DateTime, Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime)
  )

  implicit val userRoleMapping: JdbcType[UserRole] with BaseTypedType[UserRole] = MappedColumnType.base[UserRole, String](_.name, UserRole.valueOf)

  implicit val productStatusMapping: JdbcType[ProductStatus] with BaseTypedType[ProductStatus] = MappedColumnType.base[ProductStatus, String](_.name, ProductStatus.valueOf)

  implicit val billStateMapper: JdbcType[BillState] with BaseTypedType[BillState] = MappedColumnType.base[BillState, String](_.name, BillState.valueOf)

  implicit val discountTypeMapper: JdbcType[DiscountType] with BaseTypedType[DiscountType] = MappedColumnType.base[DiscountType, String](_.name, DiscountType.valueOf)

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

  class ProductCategories(tag: Tag) extends Table[ProductCategory](tag, "product_categories") {
    def * : ProvenShape[ProductCategory] = {
      val props = (id.?, name, superCategoryId, maxDiscount)

      props <> (ProductCategory.tupled, ProductCategory.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def superCategoryId: Rep[Option[Long]] = column[Option[Long]]("super_category")

    def maxDiscount: Rep[Double] = column[Double]("max_discount")

    def superCategory: ForeignKeyQuery[ProductCategories, ProductCategory] = foreignKey("super_category_fk", superCategoryId, productCategories)(_.id.?)
  }

  class Products(tag: Tag) extends Table[Product](tag, "products") {
    def * : ProvenShape[Product] = {
      val props = (id.?, name, categoryId, price, quantity, createdAt, fillStock, status, minQuantity)

      props <> (Product.tupled, Product.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def categoryId: Rep[Long] = column[Long]("category")

    def price: Rep[Double] = column[Double]("price")

    def quantity: Rep[Long] = column[Long]("quantity")

    def createdAt: Rep[DateTime] = column[DateTime]("created_at")

    def fillStock: Rep[Boolean] = column[Boolean]("fill_stock")

    def status: Rep[ProductStatus] = column[ProductStatus]("status")

    def minQuantity: Rep[Long] = column[Long]("min_quantity")

    def category: ForeignKeyQuery[ProductCategories, ProductCategory] = foreignKey("category_fk", categoryId, productCategories)(_.id)
  }

  class Bills(tag: Tag) extends Table[Bill](tag, "bills") {
    def * : ProvenShape[Bill] = {
      val props = (id.?, createdAt, customerId, state, amount, discount, discountAmount, pointsSpent, pointsGained)

      props <> (Bill.tupled, Bill.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def createdAt: Rep[DateTime] = column[DateTime]("created_at")

    def customerId: Rep[Long] = column[Long]("customer")

    def state: Rep[BillState] = column[BillState]("state")

    def amount: Rep[Double] = column[Double]("amount")

    def discount: Rep[Double] = column[Double]("discount")

    def discountAmount: Rep[Double] = column[Double]("discount_amount")

    def pointsSpent: Rep[Long] = column[Long]("points_spent")

    def pointsGained: Rep[Long] = column[Long]("points_gained")

    def customer: ForeignKeyQuery[Users, User] = foreignKey("customer_fk", customerId, users)(_.id)
  }

  class BillItems(tag: Tag) extends Table[Item](tag, "bill_items") {
    def * : ProvenShape[Item] = {
      val props = (id.?, ordinal, productId, billId, price, quantity, amount, discount, discountAmount)

      props <> (Item.tupled, Item.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def ordinal: Rep[Int] = column[Int]("ordinal")

    def productId: Rep[Long] = column[Long]("product")

    def billId: Rep[Long] = column[Long]("bill")

    def price: Rep[Double] = column[Double]("price")

    def quantity: Rep[Int] = column[Int]("quantity")

    def amount: Rep[Double] = column[Double]("amount")

    def discount: Rep[Double] = column[Double]("discount")

    def discountAmount: Rep[Double] = column[Double]("discount_amount")

    def product: ForeignKeyQuery[Products, Product] = foreignKey("product_fk", productId, products)(_.id)

    def bill: ForeignKeyQuery[Bills, Bill] = foreignKey("bill_i_id", billId, bills)(_.id)
  }

  class BillDiscounts(tag: Tag) extends Table[BillDiscount](tag, "bill_discounts") {
    def * : ProvenShape[BillDiscount] = {
      val props = (id.?, billId, discount, `type`)

      props <> (BillDiscount.tupled, BillDiscount.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def billId: Rep[Long] = column[Long]("bill")

    def discount: Rep[Double] = column[Double]("discount")

    def `type`: Rep[DiscountType] = column[DiscountType]("discount_type")

    def bill: ForeignKeyQuery[Bills, Bill] = foreignKey("bill_d_fk", billId, bills)(_.id)
  }

  class ItemDiscounts(tag: Tag) extends Table[ItemDiscount](tag, "item_discounts") {
    def * : ProvenShape[ItemDiscount] = {
      val props = (id.?, itemId, discount, `type`)

      props <> (ItemDiscount.tupled, ItemDiscount.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def itemId: Rep[Long] = column[Long]("item")

    def discount: Rep[Double] = column[Double]("discount")

    def `type`: Rep[DiscountType] = column[DiscountType]("discount_type")

    def item: ForeignKeyQuery[BillItems, Item] = foreignKey("item_fk", itemId, billItems)(_.id)
  }

}
