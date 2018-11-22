package cf.jrozen.faker.api.users

import cats.data.EitherT
import cats.{Functor, Monad}
import cf.jrozen.faker.api.{UserAlreadyExistsError, UserNotFoundError}
import cf.jrozen.faker.model.User
import cf.jrozen.faker.mongo.repository.UsersRepository

class UserService[F[_] : Monad : Functor](usersRepository: UsersRepository[F], userValidationAlgebra: UserValidationAlgebra[F]) {

  def addUser(userRequest: UserRequest): EitherT[F, UserAlreadyExistsError, User] = {
    userValidationAlgebra.doesNotExist(userRequest).flatMap { _ =>
      val user = createUser(userRequest)
      EitherT.liftF(usersRepository.save(user)).map(_ => user)
    }
  }

  def getUser(userId: String): EitherT[F, UserNotFoundError.type, User] = ???

  def createUser(userRequest: UserRequest): User = User(name = userRequest.name)

}

object UserService {
  def apply[F[_] : Monad](usersRepository: UsersRepository[F], userValidationAlgebra: UserValidationAlgebra[F]): UserService[F] =
    new UserService[F](usersRepository, userValidationAlgebra)
}