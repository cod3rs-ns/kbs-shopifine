package domain

import org.joda.time.DateTime

case class ActionDiscount(id: Option[Long], name: String, from: DateTime, to: DateTime, discount: Double)
