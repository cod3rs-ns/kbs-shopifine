package domain

case class Item(id: Long,
                ordinal: Int,
                product: Product,
                price: Double,
                quantity: Int,
                amount: Double,
                discount: Double,
                discountAmount: Double,
                discounts: Seq[ItemDiscount])
