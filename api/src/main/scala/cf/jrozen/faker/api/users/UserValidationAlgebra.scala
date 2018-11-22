package cf.jrozen.faker.api.users

import cats.data.EitherT
import cf.jrozen.faker.api.{UserAlreadyExistsError, UserNotFoundError}

import scala.language.higherKinds

trait UserValidationAlgebra[F[_]] {

  def doesNotExist(user: UserRequest): EitherT[F, UserAlreadyExistsError, Unit]

  def exists(userId: Option[Long]): EitherT[F, UserNotFoundError.type, Unit]
}
