package com.dmarjanovic.drools

import com.dmarjanovic.drools.domain._
import org.joda.time.DateTime
import org.scalatest.{MustMatchers, WordSpecLike}

class BillItemsRuleEngineSpec extends WordSpecLike with MustMatchers {

  "Bill Items Rule Engine" when {
    "creating 'BASIC' discounts" should {
      "create only discount based on quantity and 'consumer goods' category" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val product: Product = singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(60))

        // Create Bill Item which quantity is > 20 and belongs to non-consumer goods category
        val item: BillItem = createBillItem(1.toLong, 100, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 10.0, `type` = DiscountType.BASIC)
      }

      "create only discount based on quantity and category name" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "Computers")
        val product: Product = singleProductWithCategory(category, lastBoughtAt = DateTime.now.minusDays(60))

        // Create Bill Item which quantity is > 5 and belongs to 'Computers' category
        val item: BillItem = createBillItem(1.toLong, 10, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 5.0, `type` = DiscountType.BASIC)
      }

      "choose bigger discount when Bill Item achieves more discounts" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "Computers")
        val product: Product = singleProductWithCategory(category, lastBoughtAt = DateTime.now.minusDays(60))

        // Create Bill Item which quantity is > 20 and belongs to 'Computers' category
        val item: BillItem = createBillItem(1.toLong, 100, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 10.0, `type` = DiscountType.BASIC)
      }

      "choose discount based on price because it's bigger than category name" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "Computers", isConsumerGoods = true)
        val product: Product = singleProductWithCategory(category, lastBoughtAt = DateTime.now.minusDays(60))

        val item: BillItem = createBillItem(1.toLong, 10, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 7.0, `type` = DiscountType.BASIC)
      }
    }

    "creating 'PRO' discounts" should {
      "return only discount of 2% based on Product last time bought" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        // Last Bought in the last 15 days
        val product: Product = singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(8))

        // Create Bill Item which quantity is < 20 and belongs to non-consumer goods category
        val item: BillItem = createBillItem(1.toLong, 10, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 2.0, `type` = DiscountType.PRO)
      }

      "return discount based on Product last time bought along with calculated basic discount" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "Computers", isConsumerGoods = true)
        val product: Product = singleProductWithCategory(category, lastBoughtAt = DateTime.now.minusDays(6))

        val item: BillItem = createBillItem(1.toLong, 10, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain allOf(
          BillItemDiscount(discount = 7.0, `type` = DiscountType.BASIC),
          BillItemDiscount(discount = 2.0, `type` = DiscountType.PRO)
        )
      }

      "return only discount based on Category products last time bought and Product last time bought" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val c: ProductCategory = nonConsumerGoodsCategory
        val category: ProductCategory = c.copy(products = Seq(
          singleProductWithCategory(c, lastBoughtAt = DateTime.now.minusDays(60)).copy(id = Some(1)),
          singleProductWithCategory(c, lastBoughtAt = DateTime.now.minusDays(10)).copy(id = Some(2)),
          singleProductWithCategory(c, lastBoughtAt = DateTime.now.minusDays(40)).copy(id = Some(3))
        ))

        val product: Product = singleProductWithCategory(category, lastBoughtAt = DateTime.now.minusDays(10)).copy(id = Some(4))

        val item: BillItem = createBillItem(1.toLong, 10, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain allOf(
          BillItemDiscount(discount = 2.0, `type` = DiscountType.PRO),
          BillItemDiscount(discount = 1.0, `type` = DiscountType.PRO)
        )
      }

      "return discount based on Category products last time bought along with Basic discount" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val c: ProductCategory = createProductCategory(1.toLong, "Computers", isConsumerGoods = true)
        val category: ProductCategory = c.copy(products = Seq(
          singleProductWithCategory(c, lastBoughtAt = DateTime.now.minusDays(60)).copy(id = Some(1)),
          singleProductWithCategory(c, lastBoughtAt = DateTime.now.minusDays(10)).copy(id = Some(2)),
          singleProductWithCategory(c, lastBoughtAt = DateTime.now.minusDays(40)).copy(id = Some(3))
        ))

        val product: Product = singleProductWithCategory(category, lastBoughtAt = DateTime.now.minusDays(40)).copy(id = Some(4))

        val item: BillItem = createBillItem(1.toLong, 10, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain allOf(
          BillItemDiscount(discount = 7.0, `type` = DiscountType.BASIC),
          BillItemDiscount(discount = 1.0, `type` = DiscountType.PRO)
        )
      }

      "return no discounts based on Product Category Buying" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val product: Product = singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(20)).copy(id = Some(4))

        val category: ProductCategory = nonConsumerGoodsCategory.copy(products = Seq(
          singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(60)).copy(id = Some(1)),
          singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(90)).copy(id = Some(2)),
          singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(40)).copy(id = Some(3)),
          product
        ))

        val item: BillItem = createBillItem(1.toLong, 10, Some(product.copy(category = Some(category))))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must be(Seq())
      }

      "return only one Action Discount based on Bill created at time" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture with ActionDiscountsFixture {
        val product: Product = singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(20)).copy(id = Some(4))

        val category: ProductCategory = nonConsumerGoodsCategory.copy(
          products = Seq(
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(60)).copy(id = Some(1)),
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(90)).copy(id = Some(2)),
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(40)).copy(id = Some(3)),
            product
          ),
          actionDiscounts = Seq(
            createActionDiscount(DateTime.now.minusDays(120), DateTime.now.minusDays(30), 23),
            createActionDiscount(DateTime.now.minusDays(60), DateTime.now.minusDays(15), 18),
            createActionDiscount(DateTime.now.minusDays(7), DateTime.now.plus(90), 8)
          )
        )

        // It should be in 3rd action discount
        val item: BillItem = createBillItem(1.toLong, 10, Some(product.copy(category = Some(category))), billCreatedAt = DateTime.now.minusDays(1))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(8.0, DiscountType.PRO)
      }

      "return more Action Discounts based on Bill created at time" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture with ActionDiscountsFixture {
        val product: Product = singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(20)).copy(id = Some(4))

        val category: ProductCategory = nonConsumerGoodsCategory.copy(
          products = Seq(
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(60)).copy(id = Some(1)),
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(90)).copy(id = Some(2)),
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(40)).copy(id = Some(3)),
            product
          ),
          actionDiscounts = Seq(
            createActionDiscount(DateTime.now.minusDays(120), DateTime.now.minusDays(30), 23),
            createActionDiscount(DateTime.now.minusDays(60), DateTime.now.minusDays(15), 18),
            createActionDiscount(DateTime.now.minusDays(7), DateTime.now.plus(90), 8)
          )
        )

        // It should be in 1st and 2nd Action Discount
        val item: BillItem = createBillItem(1.toLong, 10, Some(product.copy(category = Some(category))), billCreatedAt = DateTime.now.minusDays(45))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain allOf(
          BillItemDiscount(23.0, DiscountType.PRO),
          BillItemDiscount(18.0, DiscountType.PRO)
        )
      }

      "return no discounts retrieved from Action Discounts" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture with ActionDiscountsFixture {
        val product: Product = singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(20)).copy(id = Some(4))

        val category: ProductCategory = nonConsumerGoodsCategory.copy(
          products = Seq(
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(60)).copy(id = Some(1)),
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(90)).copy(id = Some(2)),
            singleProductWithCategory(nonConsumerGoodsCategory, lastBoughtAt = DateTime.now.minusDays(40)).copy(id = Some(3)),
            product
          ),
          actionDiscounts = Seq(
            createActionDiscount(DateTime.now.minusDays(120), DateTime.now.minusDays(30), 23),
            createActionDiscount(DateTime.now.minusDays(60), DateTime.now.minusDays(15), 18),
            createActionDiscount(DateTime.now.minusDays(7), DateTime.now.plus(90), 8)
          )
        )

        // It shouldn't be in any Action Discount
        val item: BillItem = createBillItem(1.toLong, 10, Some(product.copy(category = Some(category))), billCreatedAt = DateTime.now.minusDays(150))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must be(Seq())
      }
    }

    "calculating final Bill Item Discount and Bill Item price" should {
      "return Bill Item with price and accumulated discounts" in new BillItemsFixture with BillItemDiscountsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "test-category-name", maxDiscount = 50)
        val product: Product = singleProductWithCategory(category, lastBoughtAt = DateTime.now.minusDays(60))
        val item: BillItem = createBillItem(1.toLong, 10, product = Some(product))

        // Add discounts
        (1 until 6).foreach(discount => item addDiscount createBillItemDiscount(discount, DiscountType.PRO))
        item.addDiscount(createBillItemDiscount(10, DiscountType.BASIC))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discount must be(25.0)
        item.quantity must be(10)
        item.price must be(1000)
        // price * quantity * discount(%)
        item.discountAmount must be(2500)
        item.amount must be(7500)
      }

      "return Bill Item with price and maximum category discount" in new BillItemsFixture with BillItemDiscountsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "test-category-name", maxDiscount = 5)
        val product: Product = singleProductWithCategory(category)
        val item: BillItem = createBillItem(1.toLong, 10, product = Some(product))

        // Add discounts
        (1 until 6).foreach(discount => item addDiscount createBillItemDiscount(discount, DiscountType.PRO))
        item.addDiscount(createBillItemDiscount(10, DiscountType.BASIC))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discount must be(5.0)
        item.quantity must be(10)
        item.price must be(1000)
        // price * quantity * discount(%)
        item.discountAmount must be(500)
        item.amount must be(9500)
      }
    }
  }

}
