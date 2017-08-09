package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo
import scala.collection.mutable

@BeanInfo
case class BillItem(id: Option[Long] = None,
                    ordinal: Int,
                    product: Option[Product] = None,
                    bill: Option[Bill] = None,
                    var price: Double,
                    quantity: Int,
                    var amount: Double = 0,
                    var discount: Double = 0,
                    var discountAmount: Double = 0,
                    var discounts: mutable.Seq[BillItemDiscount] = mutable.Seq()) {

  def getProduct: Product = product.get

  def getBill: Bill = bill.get

  def addDiscount(discount: BillItemDiscount): Unit =
    discounts = discounts :+ discount

}
