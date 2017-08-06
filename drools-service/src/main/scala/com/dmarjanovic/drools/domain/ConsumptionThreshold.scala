package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class ConsumptionThreshold(id: Option[Long] = None, from: Int, to: Int, award: Double)
