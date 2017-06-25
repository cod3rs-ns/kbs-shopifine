package domain

case class ConsumptionThreshold(id: Option[Long] = None, from: Int, to: Int, award: Double)
