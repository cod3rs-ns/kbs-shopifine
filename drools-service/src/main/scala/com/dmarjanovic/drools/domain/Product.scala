package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class Product(id: Option[Long] = None,
                   name: String,
                   imageUrl: String = "",
                   category: Option[ProductCategory] = None,
                   price: Double = 0,
                   quantity: Long = 0,
                   createdAt: DateTime,
                   var fillStock: Boolean = false,
                   status: ProductStatus = ProductStatus.ACTIVE,
                   minQuantity: Long = 0) {

  def getCategory: ProductCategory = category.get

}