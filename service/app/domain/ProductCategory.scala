package domain

case class ProductCategory(id: Option[Long] = None,
                           name: String,
                           superCategory: Option[ProductCategory] = None,
                           maximumDiscount: Double)
