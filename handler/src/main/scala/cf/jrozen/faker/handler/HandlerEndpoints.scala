package cf.jrozen.faker.handler

import java.time.Instant

import cats.Functor
import cats.effect.{Sync, Timer}
import cats.implicits._
import cf.jrozen.faker.model.domain.Call
import cf.jrozen.faker.mongo.repository.EndpointRepository
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.log4s.getLogger

class HandlerEndpoints[F[_] : Functor : Timer](
                                                handlerService: HandlerNotificationsService[F],
                                                endpointRepository: EndpointRepository[F]
                                              )(implicit
                                                S: Sync[F],
                                              ) extends Http4sDsl[F] {

  private[this] val logger = getLogger

  def endpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@_ -> Root / workspaceId / endpointId =>
      for {
        result <- RequestTranslator[F].translate(req)
        call = Call(workspaceId, endpointId, Instant.now, result)
        _ <- S.suspend(handlerService.emit(call))
        endpoint <- endpointRepository.findEndpoint(workspaceId, endpointId).flatMap(S.fromOption(_, new NoSuchElementException)) // fixme
        response <- ResponseTranslator[F].apply(endpoint.responseTemplate)
      } yield response
  }
}

object HandlerEndpoints {
  def apply[F[_] : Sync : Timer](
                                  handlerService: HandlerNotificationsService[F],
                                  endpointRepository: EndpointRepository[F]
                                ): HttpRoutes[F] = {
    new HandlerEndpoints[F](handlerService, endpointRepository).endpoints
  }
}
