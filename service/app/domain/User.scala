package domain

import org.joda.time.DateTime

case class User(id: Option[Long] = None,
                username: String,
                password: String,
                firstName: String,
                lastName: String,
                role: UserRole,
                address: Option[String],
                buyerCategoryId: Option[Long] = None,
                points: Option[Long],
                registeredAt: DateTime,
                googleAccountId: Option[String] = None)
