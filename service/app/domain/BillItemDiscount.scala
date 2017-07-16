package domain

case class BillItemDiscount(id: Option[Long] = None, itemId: Long, discount: Double, `type`: DiscountType)
