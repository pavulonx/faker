package cf.jrozen.faker.handler

import java.time.Instant

import cats.Functor
import cats.effect.{Sync, Timer}
import cats.implicits._
import cf.jrozen.faker.model.domain.{Call, ResponseTemplate}
import io.circe.syntax._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Method}
import org.log4s.getLogger

import scala.concurrent.duration._

class HandlerEndpoints[F[_] : Functor : Timer](handlerService: HandlerNotificationsService[F])(implicit S: Sync[F]) extends Http4sDsl[F] {

  private[this] val logger = getLogger

  def endpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@(m: Method) -> Root / workspaceId / endpointId =>
      for {
        result <- RequestTranslator[F].translate(req)
        call = Call(workspaceId, endpointId, Instant.now, result)
        _ <- S.suspend(handlerService.emit(call))
        response <- ResponseTranslator[F].apply(ResponseTemplate(200, "application/json", result.asJson.toString().some, 3 seconds))
      } yield response
  }
}

object HandlerEndpoints {
  def apply[F[_] : Sync : Timer](handlerService: HandlerNotificationsService[F]): HttpRoutes[F] = {
    new HandlerEndpoints[F](handlerService).endpoints
  }
}
