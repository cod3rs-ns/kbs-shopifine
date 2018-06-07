package domain

import org.joda.time.DateTime

case class Bill(
    id: Option[Long] = None,
    createdAt: DateTime,
    customerId: Long,
    state: BillState,
    totalItems: Long,
    amount: Double,
    discount: Double,
    discountAmount: Double,
    pointsSpent: Long,
    pointsGained: Long,
    address: Option[String] = None,
    longitude: Option[Double] = None,
    latitude: Option[Double] = None
)
