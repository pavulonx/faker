package cf.jrozen.faker.api.users

import cats.Functor
import cats.effect.Effect
import cats.implicits._
import cf.jrozen.faker.api.{UserAlreadyExistsError, UserNotFoundError}
import cf.jrozen.faker.model.User
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class UserEndpoints[F[_] : Effect : Functor] extends Http4sDsl[F] {

  implicit val userRequestDecoder: EntityDecoder[F, UserRequest] = jsonOf[F, UserRequest]

  def getUserEndpoint(userService: UserService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "user" / userId =>
      userService.getUser(userId).value >>= {
        case Right(u: User) => Ok(u.asJson)
        case Left(UserNotFoundError) => NotFound()
      }
  }

  def addUserEndpoint(userService: UserService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "user" =>
      req.as[UserRequest] >>= {
        userRequest => userService.addUser(userRequest).value
      } >>= {
        case Right(u: User) => Ok(u.asJson)
        case Left(UserAlreadyExistsError(u)) => Conflict(u.asJson)
      }
  }

  def endpoints(userService: UserService[F]): HttpRoutes[F] =
    getUserEndpoint(userService) <+> addUserEndpoint(userService)

}

object UserEndpoints {
  def endpoints[F[_] : Effect](usersService: UserService[F]): HttpRoutes[F] =
    new UserEndpoints[F]().endpoints(usersService)
}
