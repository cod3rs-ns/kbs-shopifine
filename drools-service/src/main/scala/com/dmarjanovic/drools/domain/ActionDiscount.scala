package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class ActionDiscount(name: String, from: DateTime, to: DateTime, discount: Double)
