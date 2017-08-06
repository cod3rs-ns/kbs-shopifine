package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class ProductCategory(id: Option[Long] = None,
                           name: String,
                           superCategory: Option[ProductCategory] = None,
                           maxDiscount: Double,
                           isConsumerGoods: Boolean = false,
                           products: Seq[Product] = Seq(),
                           actionDiscounts: Seq[ActionDiscount] = Seq()) {

  def getSuperCategory: ProductCategory = superCategory.get

}
