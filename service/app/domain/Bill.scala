package domain

import org.joda.time.DateTime

case class Bill(id: Option[Long],
                createdAt: DateTime,
                customerId: Long,
                state: BillState,
                amount: Double,
                discount: Double,
                discountAmount: Double,
                pointsSpent: Long,
                pointsGained: Long)
