package domain

case class BillDiscount(id: Option[Long] = None, billId: Long, discount: Double, `type`: DiscountType)
