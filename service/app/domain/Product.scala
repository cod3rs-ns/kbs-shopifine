package domain

import org.joda.time.DateTime

case class Product(id: Option[Long] = None,
                   name: String,
                   imageUrl: String,
                   categoryId: Long,
                   price: Double,
                   quantity: Long,
                   createdAt: DateTime,
                   lastBoughtAt: Option[DateTime] = None,
                   fillStock: Boolean = false,
                   status: ProductStatus = ProductStatus.ACTIVE,
                   minQuantity: Long,
                   isInWishlist: Boolean = false)
