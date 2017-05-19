package domain

case class BillDiscount(id: Long, bill: Bill, discount: Double, `type`: DiscountType)
