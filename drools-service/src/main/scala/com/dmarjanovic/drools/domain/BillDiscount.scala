package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BillDiscount(discount: Double, `type`: DiscountType)
