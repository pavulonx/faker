package cf.jrozen.faker.api

import cf.jrozen.faker.model.User


sealed trait ValidationError extends Product with Serializable
case class UserAlreadyExistsError(user: User) extends ValidationError
case object UserNotFoundError extends ValidationError
