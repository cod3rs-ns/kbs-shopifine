package domain

case class BillDiscount(id: Option[Long], billId: Long, discount: Double, `type`: DiscountType)
