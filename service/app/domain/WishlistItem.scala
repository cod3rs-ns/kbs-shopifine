package domain

import org.joda.time.DateTime

case class WishlistItem(id: Option[Long] = None, productId: Long, customerId: Long, createdAt: DateTime)
