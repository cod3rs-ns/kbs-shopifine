package domain

case class ConsumptionThreshold(id: Option[Long] = None, buyerCategory: Long, from: Int, to: Int, award: Double)
