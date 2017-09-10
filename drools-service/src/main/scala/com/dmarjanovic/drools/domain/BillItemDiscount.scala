package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BillItemDiscount(name: String, discount: Double, `type`: DiscountType)
