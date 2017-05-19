package domain

case class ProductCategory(id: Long, name: String, superCategory: Option[ProductCategory], maximumDiscount: Double)
