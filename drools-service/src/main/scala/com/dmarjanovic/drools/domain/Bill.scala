package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class Bill(id: Option[Long] = None,
                createdAt: DateTime,
                customer: Option[User] = None,
                var amount: Double,
                var discount: Double,
                var discountAmount: Double,
                pointsSpent: Long,
                var pointsGained: Long,
                var items: Seq[BillItem] = Seq(),
                var discounts: Seq[BillDiscount] = Seq()) {

  def getCustomer: User = customer.get

  def addDiscount(discount: BillDiscount): Unit =
    discounts = discounts :+ discount

  def addBillItem(item: BillItem): Unit =
    items = items :+ item

}
