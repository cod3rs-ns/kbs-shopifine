package com.dmarjanovic.drools.domain

import org.joda.time.DateTime

import scala.beans.BeanInfo

@BeanInfo
case class ActionDiscount(id: Option[Long] = None,
                          name: String,
                          from: DateTime,
                          to: DateTime,
                          discount: Double,
                          categories: Seq[ProductCategory] = Seq())
