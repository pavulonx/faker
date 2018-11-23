package cf.jrozen.faker.api.call

import cats.effect.{Effect, Sync}
import cats.implicits._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class CallEndpoints[F[_] : Sync] extends Http4sDsl[F] {

  private def listCalls(service: CallService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "call" / workspaceName / endpointId =>
      service.findCalls(workspaceName, endpointId) >>= (
        calls => Ok(calls.asJson)
        )
  }

  def endpoints(service: CallService[F]): HttpRoutes[F] = {
    listCalls(service)
  }

}

object CallEndpoints {
  def endpoints[F[_] : Effect](service: CallService[F]): HttpRoutes[F] =
    new CallEndpoints[F]().endpoints(service)
}