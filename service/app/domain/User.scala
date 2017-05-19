package domain

import org.joda.time.DateTime

case class User(id: Long,
                username: String,
                password: String,
                firstName: String,
                lastName: String,
                role: UserRole,
                registeredAt: DateTime)
