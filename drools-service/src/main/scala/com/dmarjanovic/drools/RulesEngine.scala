package com.dmarjanovic.drools

import com.dmarjanovic.drools.domain.{BillItem, Product}

object RulesEngine {

  def determineProductWeNeedToFillStock(products: List[Product]): Unit = {
    val session = Kie.newSession

    products map session.insert

    session.fireAllRules()
    session.dispose()
  }

  def calculateBillItemDiscounts(item: BillItem): Unit = {
    val session = Kie.newSession

    session.insert(item)

    session.fireAllRules()
    session.dispose()
  }

}

