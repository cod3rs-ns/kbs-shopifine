package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class BillItem(id: Option[Long] = None,
                    ordinal: Int,
                    product: Option[Product] = None,
                    bill: Option[Bill] = None,
                    price: Double,
                    quantity: Int,
                    amount: Double,
                    discount: Double,
                    discountAmount: Double,
                    discounts: Seq[BillItemDiscount] = Seq()) {

  def getProduct: Product = product.get

  def getBill: Bill = bill.get

}
