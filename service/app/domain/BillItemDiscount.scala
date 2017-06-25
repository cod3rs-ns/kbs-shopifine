package domain

case class BillItemDiscount(id: Option[Long], itemId: Long, discount: Double, `type`: DiscountType)
