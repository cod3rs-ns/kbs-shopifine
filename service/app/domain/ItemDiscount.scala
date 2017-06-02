package domain

case class ItemDiscount(id: Option[Long], itemId: Long, discount: Double, `type`: DiscountType)
