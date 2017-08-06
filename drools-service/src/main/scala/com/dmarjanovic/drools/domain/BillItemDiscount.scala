package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BillItemDiscount(id: Option[Long] = None,
                            item: Option[BillItem] = None,
                            discount: Double,
                            `type`: DiscountType) {

  def getItem: BillItem = item.get

}
