package domain

import org.joda.time.DateTime

case class Bill(id: Long,
                createdAt: DateTime,
                customer: User,
                state: BillState,
                amount: Double,
                discount: Double,
                discountAmount: Double,
                pointsSpent: Long,
                pointsGained: Long,
                products: Seq[Item],
                discounts: Seq[BillDiscount])
