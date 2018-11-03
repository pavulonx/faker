package cf.jrozen.faker.handler

import cats.effect.Sync
import org.http4s.dsl._
import org.http4s.{HttpRoutes, Method, Response}


class HandlerEndpoints[F[_]](implicit S: Sync[F]) extends Http4sDsl[F] {


  def endpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case (m: Method) -> Root / requestId => {
      //      Ok()
      S.delay(Response())
    }
  }

}

object HandlerEndpoints {
  def endpoints[F[_] : Sync]: HttpRoutes[F] = {
    new HandlerEndpoints[F]().endpoints
  }
}

