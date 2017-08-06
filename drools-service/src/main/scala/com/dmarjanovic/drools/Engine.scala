package com.dmarjanovic.drools

import domain.{Bill, Product}
import java.util

import scala.collection.JavaConversions._

object Engine {

  def determineProductWeNeedToFillStock(products: List[Product]): Unit = {
    val session = Kie.newSession

    products map session.insert

    session.fireAllRules()
    session.dispose()
  }

}

