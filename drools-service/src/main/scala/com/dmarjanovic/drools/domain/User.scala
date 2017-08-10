package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class User(id: Option[Long] = None,
                username: String,
                firstName: String,
                lastName: String,
                role: UserRole,
                address: Option[String],
                buyerCategory: Option[BuyerCategory] = None,
                points: Option[Long] = None,
                registeredAt: DateTime) {

  def getBuyerCategory: BuyerCategory = buyerCategory.get

  def getPoints: Long = points.get

}
