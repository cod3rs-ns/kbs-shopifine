package db

import java.sql.Timestamp

import domain._
import org.joda.time.DateTime
import slick.ast.BaseTypedType
import slick.driver.MySQLDriver.api._
import slick.jdbc.JdbcType
import slick.lifted.{ForeignKeyQuery, ProvenShape, TableQuery}

trait DatabaseSchema {
  val buyerCategories: TableQuery[BuyerCategories] = TableQuery[BuyerCategories]
  val users: TableQuery[Users] = TableQuery[Users]

  val productCategories: TableQuery[ProductCategories] = TableQuery[ProductCategories]
  val products: TableQuery[Products] = TableQuery[Products]

  val bills: TableQuery[Bills] = TableQuery[Bills]
  val billItems: TableQuery[BillItems] = TableQuery[BillItems]
  val billDiscounts: TableQuery[BillDiscounts] = TableQuery[BillDiscounts]
  val itemDiscounts: TableQuery[ItemDiscounts] = TableQuery[ItemDiscounts]

  val consumptionThresholds: TableQuery[ConsumptionThresholds] = TableQuery[ConsumptionThresholds]
  val actionDiscounts: TableQuery[ActionDiscounts] = TableQuery[ActionDiscounts]
  val actionDiscountsProductCategories: TableQuery[ActionDiscountsProductCategories] = TableQuery[ActionDiscountsProductCategories]

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
      val props = (id.?, username, password, firstName, lastName, role, address, buyerCategory, points, registeredAt, googleAccountId)

      props <> (User.tupled, User.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def password: Rep[String] = column[String]("password")

    def firstName: Rep[String] = column[String]("first_name")

    def lastName: Rep[String] = column[String]("last_name")

    def role: Rep[UserRole] = column[UserRole]("role")

    def address: Rep[Option[String]] = column[Option[String]]("address")

    def buyerCategory: Rep[Option[Long]] = column[Option[Long]]("buyer_category_id")

    def buyerCategoryFK: ForeignKeyQuery[BuyerCategories, BuyerCategory] = foreignKey("fk_users_buyer_categories_id", buyerCategory, buyerCategories)(category =>
      category.id.?, onDelete = ForeignKeyAction.Cascade
    )

    def points: Rep[Option[Long]] = column[Option[Long]]("points")

    def registeredAt: Rep[DateTime] = column[DateTime]("registered_at")

    def googleAccountId: Rep[Option[String]] = column[Option[String]]("google_account_id")
  }

  class ProductCategories(tag: Tag) extends Table[ProductCategory](tag, "product_categories") {
    def * : ProvenShape[ProductCategory] = {
      val props = (id.?, name, superCategory, maxDiscount, isConsumerGoods)

      props <> (ProductCategory.tupled, ProductCategory.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def superCategory: Rep[Option[Long]] = column[Option[Long]]("super_category_id")

    def superCategoryFK: ForeignKeyQuery[ProductCategories, ProductCategory] = foreignKey("fk_product_categories_product_categories_id", superCategory, productCategories)(
      category => category.id.?, onDelete = ForeignKeyAction.Cascade
    )

    def maxDiscount: Rep[Double] = column[Double]("max_discount")

    def isConsumerGoods: Rep[Boolean] = column[Boolean]("is_consumer_goods")
  }

  class Products(tag: Tag) extends Table[Product](tag, "products") {
    def * : ProvenShape[Product] = {
      val props = (id.?, name, imageUrl, productCategory, price, quantity, createdAt, lastBoughtAt.?, fillStock, status, minQuantity)

      props <> (Product.tupled, Product.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def imageUrl: Rep[String] = column[String]("image_url")

    def productCategory: Rep[Long] = column[Long]("product_category_id")

    def price: Rep[Double] = column[Double]("price")

    def quantity: Rep[Long] = column[Long]("quantity")

    def createdAt: Rep[DateTime] = column[DateTime]("created_at")

    def lastBoughtAt: Rep[DateTime] = column[DateTime]("last_bought_at")

    def fillStock: Rep[Boolean] = column[Boolean]("fill_stock")

    def status: Rep[ProductStatus] = column[ProductStatus]("status")

    def minQuantity: Rep[Long] = column[Long]("min_quantity")

    def productCategoryFK: ForeignKeyQuery[ProductCategories, ProductCategory] = foreignKey("fk_products_product_categories_id", productCategory, productCategories)(category =>
      category.id, onDelete = ForeignKeyAction.Cascade
    )
  }

  class Bills(tag: Tag) extends Table[Bill](tag, "bills") {
    def * : ProvenShape[Bill] = {
      val props = (id.?, createdAt, customer, state, totalItems, amount, discount, discountAmount, pointsSpent, pointsGained)

      props <> (Bill.tupled, Bill.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def createdAt: Rep[DateTime] = column[DateTime]("created_at")

    def customer: Rep[Long] = column[Long]("customer")

    def customerFK: ForeignKeyQuery[Users, User] = foreignKey("fk_bills_users_id", customer, users)(user =>
      user.id, onDelete = ForeignKeyAction.Cascade
    )

    def state: Rep[BillState] = column[BillState]("state")

    def totalItems: Rep[Long] = column[Long]("total_items")

    def amount: Rep[Double] = column[Double]("amount")

    def discount: Rep[Double] = column[Double]("discount")

    def discountAmount: Rep[Double] = column[Double]("discount_amount")

    def pointsSpent: Rep[Long] = column[Long]("points_spent")

    def pointsGained: Rep[Long] = column[Long]("points_gained")
  }

  class BillItems(tag: Tag) extends Table[BillItem](tag, "bill_items") {
    def * : ProvenShape[BillItem] = {
      val props = (id.?, ordinal, product, bill, price, quantity, amount, discount, discountAmount)

      props <> (BillItem.tupled, BillItem.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def ordinal: Rep[Int] = column[Int]("ordinal")

    def product: Rep[Long] = column[Long]("product_id")

    def productFK: ForeignKeyQuery[Products, Product] = foreignKey("fk_bill_items_products_id", product, products)(product =>
      product.id, onDelete = ForeignKeyAction.Cascade
    )

    def bill: Rep[Long] = column[Long]("bill_id")

    def billFK: ForeignKeyQuery[Bills, Bill] = foreignKey("fk_bill_items_bills_id", bill, bills)(bill =>
      bill.id, onDelete = ForeignKeyAction.Cascade
    )

    def price: Rep[Double] = column[Double]("price")

    def quantity: Rep[Int] = column[Int]("quantity")

    def amount: Rep[Double] = column[Double]("amount")

    def discount: Rep[Double] = column[Double]("discount")

    def discountAmount: Rep[Double] = column[Double]("discount_amount")
  }

  class BillDiscounts(tag: Tag) extends Table[BillDiscount](tag, "bill_discounts") {
    def * : ProvenShape[BillDiscount] = {
      val props = (id.?, bill, name, discount, `type`)

      props <> (BillDiscount.tupled, BillDiscount.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def bill: Rep[Long] = column[Long]("bill_id")

    def billFK: ForeignKeyQuery[Bills, Bill] = foreignKey("fk_bill_discounts_bills_id", bill, bills)(bill =>
      bill.id, onDelete = ForeignKeyAction.Cascade
    )

    def name: Rep[String] = column[String]("name")

    def discount: Rep[Double] = column[Double]("discount")

    def `type`: Rep[DiscountType] = column[DiscountType]("discount_type")
  }

  class ItemDiscounts(tag: Tag) extends Table[BillItemDiscount](tag, "item_discounts") {
    def * : ProvenShape[BillItemDiscount] = {
      val props = (id.?, item, name, discount, `type`)

      props <> (BillItemDiscount.tupled, BillItemDiscount.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def item: Rep[Long] = column[Long]("item_id")

    def itemFK: ForeignKeyQuery[BillItems, BillItem] = foreignKey("fk_item_discounts_items_id", item, billItems)(item =>
      item.id, ForeignKeyAction.Cascade
    )

    def name: Rep[String] = column[String]("name")

    def discount: Rep[Double] = column[Double]("discount")

    def `type`: Rep[DiscountType] = column[DiscountType]("discount_type")
  }

  class ConsumptionThresholds(tag: Tag) extends Table[ConsumptionThreshold](tag, "consumption_thresholds") {
    def * : ProvenShape[ConsumptionThreshold] = {
      val props = (id.?, buyerCategory, from, to, award)

      props <> (ConsumptionThreshold.tupled, ConsumptionThreshold.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def buyerCategory: Rep[Long] = column[Long]("buyer_category_id")

    def buyerCategoryFK: ForeignKeyQuery[BuyerCategories, BuyerCategory] = foreignKey("fk_consumption_thresholds_buyer_categories_id", buyerCategory, buyerCategories)(category =>
      category.id, ForeignKeyAction.Cascade
    )

    def from: Rep[Int] = column[Int]("from")

    def to: Rep[Int] = column[Int]("to")

    def award: Rep[Double] = column[Double]("award")
  }

  class ActionDiscounts(tag: Tag) extends Table[ActionDiscount](tag, "action_discounts") {
    def * : ProvenShape[ActionDiscount] = {
      val props = (id.?, name, from, to, discount)

      props <> (ActionDiscount.tupled, ActionDiscount.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name: Rep[String] = column[String]("name")

    def from: Rep[DateTime] = column[DateTime]("from")

    def to: Rep[DateTime] = column[DateTime]("to")

    def discount: Rep[Double] = column[Double]("discount")
  }

  class ActionDiscountsProductCategories(tag: Tag) extends Table[ActionDiscountProductCategory](tag, "action_discounts_product_categories") {
    def * : ProvenShape[ActionDiscountProductCategory] = {
      val props = (id.?, discount, category)

      props <> (ActionDiscountProductCategory.tupled, ActionDiscountProductCategory.unapply)
    }

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def discount: Rep[Long] = column[Long]("discount_id")

    def discountFK: ForeignKeyQuery[ActionDiscounts, ActionDiscount] = foreignKey("fk_action_discounts_product_categories_action_discounts_id", discount, actionDiscounts)(discount =>
      discount.id, onDelete = ForeignKeyAction.Cascade
    )

    def category: Rep[Long] = column[Long]("category_id")

    def categoryFK: ForeignKeyQuery[ProductCategories, ProductCategory] = foreignKey("fk_action_discounts_product_categories_product_categories_id", category, productCategories)(category =>
      category.id, onDelete = ForeignKeyAction.Cascade
    )
  }

}
