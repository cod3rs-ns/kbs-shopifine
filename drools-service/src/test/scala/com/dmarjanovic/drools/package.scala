package com.dmarjanovic

import com.dmarjanovic.drools.domain._
import org.joda.time.DateTime

package object drools {

  trait ProductsFixture {
    def createProduct(seed: Long, quantity: Long, minQuantity: Long, fillStock: Boolean = false, category: Option[ProductCategory] = None): Product =
      Product(
        category = category,
        quantity = quantity,
        minQuantity = minQuantity,
        fillStock = fillStock,
        lastBoughtAt = DateTime.now()
      )

    def singleProductWithCategory(category: ProductCategory): Product =
      Product(
        category = Some(category),
        quantity = 0,
        minQuantity = 0,
        lastBoughtAt = DateTime.now()
      )
  }

  trait BillItemsFixture {
    def createBillItem(seed: Long, quantity: Int, product: Option[Product] = None): BillItem =
      BillItem(
        price = 1000,
        quantity = quantity,
        discount = seed,
        amount = quantity * 1000 * (100.0 - seed * 2) / 100,
        product = product
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

}
