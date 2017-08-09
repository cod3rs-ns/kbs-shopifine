package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class Bill(id: Option[Long] = None,
                createdAt: DateTime,
                customer: Option[User] = None,
                state: BillState,
                var amount: Double,
                var discount: Double = 0,
                var discountAmount: Double = 0,
                pointsSpent: Long = 0,
                var pointsGained: Long = 0,
                var items: Seq[BillItem] = Seq(),
                var discounts: Seq[BillDiscount] = Seq()) {

  def getCustomer: User = customer.get

  def addDiscount(discount: BillDiscount): Unit =
    discounts = discounts :+ discount

  def addBillItem(item: BillItem): Unit =
    items = items :+ item

}
