package cf.jrozen.faker.handler

import cats.effect.Sync
import org.http4s.dsl._
import org.http4s.{HttpRoutes, Method, Response}

class HandlerEndpoints[F[_]](handlerService: HandlerService[F])(implicit S: Sync[F]) extends Http4sDsl[F] {


  def endpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@(m: Method) -> Root / requestId => {
      //      Ok()
      val value = s"$m   ${req.remoteAddr.getOrElse("unknown")} ${req.remote} $requestId ${req.toString()}"
      //      S.suspend(handlerService.emit(value))
      S.delay(Response(Accepted).withEntity(value))
      //      NoContent()
    }
  }

}

object HandlerEndpoints {
  def endpoints[F[_] : Sync](handlerService: HandlerService[F]): HttpRoutes[F] = {
    new HandlerEndpoints[F](handlerService).endpoints
  }
}

