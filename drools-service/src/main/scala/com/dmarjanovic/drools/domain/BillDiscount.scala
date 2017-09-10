package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BillDiscount(name: String, discount: Double, `type`: DiscountType)
