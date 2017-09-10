package domain

case class BillDiscount(id: Option[Long] = None, billId: Long, name: String, discount: Double, `type`: DiscountType)
