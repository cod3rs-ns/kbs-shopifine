package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class ConsumptionThreshold(from: Int, to: Int, award: Double)
