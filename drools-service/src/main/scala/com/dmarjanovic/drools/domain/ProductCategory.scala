package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class ProductCategory(name: String,
                           maxDiscount: Double,
                           isConsumerGoods: Boolean = false,
                           products: Seq[Product] = Seq(),
                           actionDiscounts: Seq[ActionDiscount] = Seq())
