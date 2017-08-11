package com.dmarjanovic.drools

import com.dmarjanovic.drools.domain.{Bill, BillItem, Product}

object RulesEngine {

  def determineProductWeNeedToFillStock(products: List[Product]): Unit = {
    val session = Kie.newSession

    products map session.insert

    session
      .getAgenda
      .getAgendaGroup(ProductsAgenda)
      .setFocus()

    session.fireAllRules()
    session.dispose()
  }

  def calculateBillItemDiscounts(item: BillItem): Unit = {
    val session = Kie.newSession

    session.insert(item)

    session
      .getAgenda
      .getAgendaGroup(BillItemsAgenda)
      .setFocus()

    session.fireAllRules()
    session.dispose()
  }

  def calculateBillDiscounts(bill: Bill): Unit = {
    val session = Kie.newSession

    session.insert(bill)

    session
      .getAgenda
      .getAgendaGroup(BillsAgenda)
      .setFocus()

    session.fireAllRules()
    session.dispose()
  }

  private val BillItemsAgenda = "bill-item-bonuses"
  private val BillsAgenda = "bill-bonuses"
  private val ProductsAgenda = "products-stock-fill"

}

