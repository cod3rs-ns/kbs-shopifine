package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BuyerCategory(name: String, thresholds: Seq[ConsumptionThreshold] = Seq())
