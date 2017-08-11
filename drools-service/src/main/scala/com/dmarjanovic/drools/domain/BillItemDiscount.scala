package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BillItemDiscount(id: Option[Long] = None,
                            discount: Double,
                            `type`: DiscountType)