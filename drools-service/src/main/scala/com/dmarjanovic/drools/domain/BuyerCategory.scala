package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BuyerCategory(id: Option[Long] = None, name: String, thresholds: Seq[ConsumptionThreshold] = Seq())
