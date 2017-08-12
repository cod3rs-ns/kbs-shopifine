package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class ActionDiscount(from: DateTime, to: DateTime, discount: Double)
