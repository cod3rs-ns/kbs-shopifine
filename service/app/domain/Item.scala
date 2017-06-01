package domain

case class Item(id: Option[Long],
                ordinal: Int,
                productId: Long,
                billId: Long,
                price: Double,
                quantity: Int,
                amount: Double,
                discount: Double,
                discountAmount: Double)
