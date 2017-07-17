package domain

case class BillItem(id: Option[Long] = None,
                    ordinal: Int,
                    productId: Long,
                    billId: Long,
                    price: Double,
                    quantity: Int,
                    amount: Double,
                    discount: Double,
                    discountAmount: Double)
