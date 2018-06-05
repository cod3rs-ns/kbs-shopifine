package domain

import org.joda.time.DateTime

case class User(id: Option[Long] = None,
                username: String,
                password: String,
                firstName: String,
                lastName: String,
                role: UserRole,
                address: Option[String] = None,
                longitude: Option[Double] = None,
                latitude: Option[Double] = None,
                buyerCategoryId: Option[Long] = None,
                points: Option[Long] = None,
                registeredAt: DateTime,
                googleAccountId: Option[String] = None)
