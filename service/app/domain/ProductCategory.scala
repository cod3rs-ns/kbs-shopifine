package domain

case class ProductCategory(id: Option[Long] = None,
                           name: String,
                           superCategoryId: Option[Long] = None,
                           maxDiscount: Double,
                           isConsumerGoods: Boolean = false)
