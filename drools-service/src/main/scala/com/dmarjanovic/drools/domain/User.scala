package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class User(registeredAt: DateTime, buyerCategory: Option[BuyerCategory] = None) {

  def getBuyerCategory: BuyerCategory = buyerCategory.get

}
