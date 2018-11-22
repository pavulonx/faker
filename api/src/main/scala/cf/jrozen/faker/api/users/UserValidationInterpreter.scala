package cf.jrozen.faker.api.users

import cats._
import cats.data.EitherT
import cats.implicits._
import cf.jrozen.faker.api.{UserAlreadyExistsError, UserNotFoundError}
import cf.jrozen.faker.mongo.repository.UsersRepository

class UserValidationInterpreter[F[_] : Monad](userRepo: UsersRepository[F]) extends UserValidationAlgebra[F] {

  def doesNotExist(user: UserRequest) = EitherT {
    userRepo.findByName(user.name).map {
      case None => Right(())
      case Some(u) => Left(UserAlreadyExistsError(u))
    }
  }

  def exists(userId: Option[Long]): EitherT[F, UserNotFoundError.type, Unit] = ???

  //
  //    EitherT {
  //      userId.map { id =>
  //        userRepo.get(id).map {
  //          case Some(_) => Right(())
  //          case _ => Left(UserNotFoundError)
  //        }
  //      }.getOrElse(
  //        Either.left[UserNotFoundError.type, Unit](UserNotFoundError).pure[F]
  //      )
  //    }
}

object UserValidationInterpreter {
  def apply[F[_] : Monad](repo: UsersRepository[F]): UserValidationAlgebra[F] =
    new UserValidationInterpreter[F](repo)
}
