package com.dmarjanovic.drools

import com.dmarjanovic.drools.domain._
import org.scalatest.{MustMatchers, WordSpecLike}

class BillItemsRuleEngineSpec extends WordSpecLike with MustMatchers {

  "Bill Items Rule Engine" when {
    "creating 'BASIC' discounts" should {
      "create only discount based on quantity and 'consumer goods' category" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val product: Product = singleProductWithCategory(nonConsumerGoodsCategory)

        // Create Bill Item which quantity is > 20 and belongs to non-consumer goods category
        val item: BillItem = createBillItem(1.toLong, 100, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 10.0, `type` = DiscountType.BASIC)
      }

      "create only discount based on quantity and category name" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "Computers")
        val product: Product = singleProductWithCategory(category)

        // Create Bill Item which quantity is > 5 and belongs to 'Computers' category
        val item: BillItem = createBillItem(1.toLong, 10, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 5.0, `type` = DiscountType.BASIC)
      }

      "choose bigger discount when Bill Item achieves more discounts" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "Computers")
        val product: Product = singleProductWithCategory(category)

        // Create Bill Item which quantity is > 20 and belongs to 'Computers' category
        val item: BillItem = createBillItem(1.toLong, 100, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 10.0, `type` = DiscountType.BASIC)
      }

      "choose discount based on price because it's bigger than category name" in new BillItemsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "Computers", isConsumerGoods = true)
        val product: Product = singleProductWithCategory(category)

        val item: BillItem = createBillItem(1.toLong, 10, Some(product))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discounts must contain only BillItemDiscount(discount = 7.0, `type` = DiscountType.BASIC)
      }
    }

    "creating 'PRO' discounts" should {
      // TODO
    }

    "calculating final Bill Item Discount and Bill Item price" should {
      "return Bill Item with price and accumulated discounts" in new BillItemsFixture with BillItemDiscountsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "test-category-name", maxDiscount = 50)
        val product: Product = singleProductWithCategory(category)
        val item: BillItem = createBillItem(1.toLong, 10, product = Some(product))

        // Add discounts
        (1 until 6).foreach(discount => item addDiscount createBillItemDiscount(discount, DiscountType.PRO))
        item.addDiscount(createBillItemDiscount(10, DiscountType.BASIC))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discount must be (25.0)
        item.quantity must be (10)
        item.price must be (1000)
        // price * quantity * discount(%)
        item.discountAmount must be (2500)
        item.amount must be (7500)
      }

      "return Bill Item with price and maximum category discount" in new BillItemsFixture with BillItemDiscountsFixture with ProductsFixture with ProductCategoriesFixture {
        val category: ProductCategory = createProductCategory(1.toLong, "test-category-name", maxDiscount = 5)
        val product: Product = singleProductWithCategory(category)
        val item: BillItem = createBillItem(1.toLong, 10, product = Some(product))

        // Add discounts
        (1 until 6).foreach(discount => item addDiscount createBillItemDiscount(discount, DiscountType.PRO))
        item.addDiscount(createBillItemDiscount(10, DiscountType.BASIC))

        RulesEngine.calculateBillItemDiscounts(item)

        item.discount must be (5.0)
        item.quantity must be (10)
        item.price must be (1000)
        // price * quantity * discount(%)
        item.discountAmount must be (500)
        item.amount must be (9500)
      }
    }
  }

}
