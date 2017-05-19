package domain

import org.joda.time.DateTime

case class Product(id: Long,
                   name: String,
                   productCategory: ProductCategory,
                   price: Double,
                   quantity: Long,
                   createdAt: DateTime,
                   fillStock: Boolean = false,
                   status: ProductStatus,
                   minQuantity: Long)