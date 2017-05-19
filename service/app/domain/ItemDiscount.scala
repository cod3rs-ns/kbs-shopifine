package domain

case class ItemDiscount(id: Long, item: Item, discount: Double, `type`: DiscountType)
