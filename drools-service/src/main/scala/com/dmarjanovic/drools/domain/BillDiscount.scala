package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BillDiscount(id: Option[Long] = None, bill: Option[Bill] = None, discount: Double, `type`: DiscountType) {

  def getBill: Bill = bill.get

}
