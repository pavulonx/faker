package cf.jrozen.faker.handler

import cats.Functor
import cats.effect.{Async, Sync, Timer}
import cats.implicits._
import cf.jrozen.faker.model.domain.ResponseTemplate
import io.circe.syntax._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Method}
import org.log4s.getLogger

import scala.concurrent.duration._

class HandlerEndpoints[F[_] : Functor : Timer](handlerService: HandlerNotificationsService[F])(implicit S: Sync[F]) extends Http4sDsl[F] {

  private[this] val logger = getLogger

  def endpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@(m: Method) -> Root / requestId =>
      for {
        result <- RequestTranslator[F].apply(req)
        _ <- S.suspend(handlerService.emit(result.asJson.toString()))
        response <- ResponseTranslator[F].apply(ResponseTemplate(200, "application/json", result.asJson.toString().some, 3 seconds))
      } yield response
  }
}

object HandlerEndpoints {
  def endpoints[F[_] : Sync :  Timer](handlerService: HandlerNotificationsService[F]): HttpRoutes[F] = {
    new HandlerEndpoints[F](handlerService).endpoints
  }
}
