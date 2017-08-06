package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class Bill(id: Option[Long] = None,
                createdAt: DateTime,
                customer: Option[User] = None,
                state: BillState,
                amount: Double,
                discount: Double,
                discountAmount: Double,
                pointsSpent: Long,
                var pointsGained: Long,
                items: Seq[BillItem] = Seq(),
                discounts: Seq[BillDiscount] = Seq()) {

  def getCustomer: User = customer.get

}
