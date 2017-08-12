package com.dmarjanovic.drools.domain

import scala.beans.BeanInfo

@BeanInfo
case class Product(id: Option[Long] = None,
                   category: Option[ProductCategory] = None,
                   quantity: Long,
                   var fillStock: Boolean = false,
                   minQuantity: Long) {

  def getCategory: ProductCategory = category.get

}