package com.dmarjanovic.drools

import com.dmarjanovic.drools.domain._
import org.joda.time.DateTime
import org.scalatest.{MustMatchers, WordSpecLike}

class BillsRuleEngineSpec extends WordSpecLike with MustMatchers {

  "Bills Rule Engine" when {
    "creating 'BASIC' discounts" should {
      "create 'BASIC' discount based on Bill amount" in new BillsFixture {
        val bill: Bill = createBill(Some(customer()), 400000)

        RulesEngine.calculateBillDiscounts(bill)

        bill.discounts must contain only BillDiscount(name = "Based on total amount.", discount = 5, `type` = DiscountType.BASIC)
      }

      "not create any 'BASIC' discount" in new BillsFixture {
        val bill: Bill = createBill(Some(customer()), 40000)

        RulesEngine.calculateBillDiscounts(bill)

        bill.discounts must be(Seq())
      }
    }

    "creating 'PRO' discounts" should {
      "create 'PRO' discount based only on Customer membership" in new BillsFixture {
        val bill: Bill = createBill(Some(customer(DateTime.now.minusYears(3))), 15000)

        RulesEngine.calculateBillDiscounts(bill)

        bill.discounts must contain only BillDiscount(name = "Based on user loyalty.", discount = 1, `type` = DiscountType.PRO)
      }

      "create 'PRO' discount based only on Customer category" in new BillsFixture {
        val bill: Bill = createBill(Some(customer(categoryName = "Gold Buyer")), 15000)

        RulesEngine.calculateBillDiscounts(bill)

        bill.discounts must contain only BillDiscount(name = "Based on user category.", discount = 1, `type` = DiscountType.PRO)
      }

      "create 'PRO' discount both based on Customer membership and category" in new BillsFixture {
        val bill: Bill = createBill(Some(customer(DateTime.now.minusYears(3), "Silver Buyer")), 15000)

        RulesEngine.calculateBillDiscounts(bill)

        bill.discounts must contain allOf(
          BillDiscount(name = "Based on user loyalty.", discount = 1, `type` = DiscountType.PRO),
          BillDiscount(name = "Based on user category.", discount = 1, `type` = DiscountType.PRO)
        )
      }

      "create all 'PRO' discounts" in new BillsFixture {
        val bill: Bill = createBill(Some(customer(DateTime.now.minusYears(3), "Silver Buyer")), 150000)

        RulesEngine.calculateBillDiscounts(bill)

        bill.discounts must contain allOf(
          BillDiscount(name = "Based on user loyalty.", discount = 1, `type` = DiscountType.PRO),
          BillDiscount(name = "Based on user category.", discount = 1, `type` = DiscountType.PRO)
        )
      }

      "create 'PRO' discount based on amount and Products" in new BillsFixture with BillItemsFixture {
        val bill: Bill = createBill(Some(customer()), 60000)

        (0 until 5).foreach(seed => bill.addBillItem(
          createBillItem(seed, seed, price = 10000)
        ))

        (6 until 10).foreach(seed => bill.addBillItem(
          createBillItem(seed, seed, price = 2000)
        ))

        RulesEngine.calculateBillDiscounts(bill)

        bill.discounts must contain only BillDiscount(name = "Based on top 10 items amount.", discount = 3, `type` = DiscountType.PRO)
      }

      "not create 'PRO' discount based on amount and Products" in new BillsFixture with BillItemsFixture {
        val bill: Bill = createBill(Some(customer()), 60000)

        (0 until 60).foreach(seed => bill.addBillItem(
          createBillItem(seed, seed, price = 100)
        ))

        RulesEngine.calculateBillDiscounts(bill)

        bill.discounts must be(Seq())
      }
    }

    "calculating final Bill Discount and Bill price" should {
      "return Bill with price and accumulated discounts" in new BillsFixture with BillItemsFixture {
        val bill: Bill = createBill(Some(customer(DateTime.now.minusYears(3), "Silver Buyer")), 150000)

        (1 until 5).foreach(index => bill.addBillItem(createBillItem(index, index * 3)))

        RulesEngine.calculateBillDiscounts(bill)

        val discount: Double = bill.discounts.map(_.discount).sum
        val billItemsSum: Double = bill.items.map(_.amount).sum

        bill.discount must be(discount)
        bill.discountAmount must be(discount / 100.0 * billItemsSum)
        bill.amount must be((1 - discount / 100.0) * billItemsSum)
      }
    }

    "calculating gained points for customer" should {
      "return Bill with gained points when it fits to thresholds" in new BillsFixture with BillItemsFixture with BuyerCategoriesFixture {
        val category: BuyerCategory = createBuyerCategory(Seq(10.0, 11.0, 15.0, 4.0))
        // Calculated Bill amount will be 28200.0

        // Should belong to first threshold (20k-30k) with discount 15.0
        val bill: Bill = createBill(Some(customerWithThresholds(category)), amount = 0)
        (1 until 5).foreach(index => bill.addBillItem(createBillItem(index, index * 3)))

        RulesEngine.calculateBillDiscounts(bill)

        bill.pointsGained must be(28200.0 * 15.0 / 100)
      }
    }
  }

  def customer(registeredAt: DateTime = DateTime.now, categoryName: String = "test-buyer-category"): User =
    User(
      buyerCategory = Some(BuyerCategory(
        name = categoryName
      )),
      registeredAt = registeredAt
    )

  def customerWithThresholds(category: BuyerCategory): User =
    User(buyerCategory = Some(category), registeredAt = DateTime.now)

}
