package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo
import scala.collection.mutable

@BeanInfo
case class BillItem(product: Option[Product] = None,
                    billCreatedAt: DateTime,
                    var price: Double,
                    quantity: Int,
                    var amount: Double = 0,
                    var discount: Double = 0,
                    var discountAmount: Double = 0,
                    var discounts: mutable.Seq[BillItemDiscount] = mutable.Seq()) {

  def getProduct: Product = product.get

  def addDiscount(discount: BillItemDiscount): Unit =
    discounts = discounts :+ discount

}
