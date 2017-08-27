package com.dmarjanovic

import com.dmarjanovic.drools.domain._
import org.joda.time.DateTime

package object drools {

  trait ProductsFixture {
    def createProduct(seed: Long, quantity: Long, minQuantity: Long, fillStock: Boolean = false, category: Option[ProductCategory] = None, lastBoughtAt: DateTime = DateTime.now): Product =
      Product(
        category = category,
        quantity = quantity,
        minQuantity = minQuantity,
        fillStock = fillStock,
        lastBoughtAt = DateTime.now()
      )

    def singleProductWithCategory(category: ProductCategory, lastBoughtAt: DateTime = DateTime.now()): Product =
      Product(
        category = Some(category),
        quantity = 0,
        minQuantity = 0,
        lastBoughtAt = lastBoughtAt
      )
  }

  trait BillItemsFixture {
    def createBillItem(seed: Long, quantity: Int, product: Option[Product] = None, billCreatedAt: DateTime = DateTime.now, price: Double = 1000): BillItem =
      BillItem(
        price = price,
        quantity = quantity,
        discount = seed,
        amount = quantity * price * (100.0 - seed * 2) / 100,
        product = product,
        billCreatedAt = billCreatedAt
      )
  }

  trait ProductCategoriesFixture {
    def createProductCategory(seed: Long, name: String, isConsumerGoods: Boolean = false, maxDiscount: Long = 10): ProductCategory =
      ProductCategory(
        name = name,
        maxDiscount = maxDiscount,
        isConsumerGoods = isConsumerGoods
      )

    val nonConsumerGoodsCategory = ProductCategory(
      name = "test-no-consumer-goods-category",
      maxDiscount = 10
    )

    val consumerGoodsCategory = ProductCategory(
      name = "test-no-consumer-goods-category",
      maxDiscount = 10
    )
  }

  trait BuyerCategoriesFixture {
    private def createThreshold(from: Int, to: Int, award: Double): ConsumptionThreshold =
      ConsumptionThreshold(
        from = from,
        to = to,
        award = award
      )

    def createBuyerCategory(awards: Seq[Double]): BuyerCategory =
      BuyerCategory(
        name = "test-buyer-category",
        thresholds = awards.zipWithIndex.map {
          case (award, index) => createThreshold(index * 10000, (index + 1) * 10000, award)
        }
      )
  }

  trait BillItemDiscountsFixture {
    def createBillItemDiscount(discount: Double, `type`: DiscountType): BillItemDiscount =
      BillItemDiscount(
        discount = discount,
        `type` = `type`
      )
  }

  trait BillsFixture {
    def createBill(customer: Option[User] = None, amount: Double): Bill =
      Bill(
        createdAt = DateTime.now,
        customer = customer,
        amount = amount,
        discount = 0,
        discountAmount = 0,
        pointsGained = 0,
        pointsSpent = 0
      )
  }

  trait ActionDiscountsFixture {
    def createActionDiscount(from: DateTime, to: DateTime, discount: Double): ActionDiscount =
      ActionDiscount(
        from = from,
        to = to,
        discount = discount
      )
  }

}
