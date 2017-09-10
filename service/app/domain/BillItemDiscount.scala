package domain

case class BillItemDiscount(id: Option[Long] = None, itemId: Long, name: String, discount: Double, `type`: DiscountType)
