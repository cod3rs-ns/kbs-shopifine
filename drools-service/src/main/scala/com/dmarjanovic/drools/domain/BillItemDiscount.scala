package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BillItemDiscount(discount: Double, `type`: DiscountType)
