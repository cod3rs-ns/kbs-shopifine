package domain

case class SalesmanCategory(id: Long, name: String, consumptionThresholds: Seq[ConsumptionThreshold])
