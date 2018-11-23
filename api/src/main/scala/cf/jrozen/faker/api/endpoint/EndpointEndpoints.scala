package cf.jrozen.faker.api.endpoint

import cats.effect.{Effect, Sync}
import cats.implicits._
import cf.jrozen.faker.api.EndpointNotFoundError
import cf.jrozen.faker.model.domain.Endpoint
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class EndpointEndpoints[F[_] : Sync] extends Http4sDsl[F] {

  implicit val endpointRequestDecoder: EntityDecoder[F, EndpointRequest] = jsonOf[F, EndpointRequest]

  private def getEndpoint(service: EndpointService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "endpoint" / workspaceName / endpointId =>
      service.getEndpoint(workspaceName, endpointId).value >>= {
        case Right(u: Endpoint) => Ok(u.asJson)
        case Left(EndpointNotFoundError(_)) => NotFound()
      }
  }

  private def addEndpoint(service: EndpointService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "endpoint" / workspaceName => for {
      endpointRequest <- req.as[EndpointRequest]
      endpoint <- service.addEndpoint(workspaceName, endpointRequest)
      response <- Created(endpoint.asJson)
    } yield response
  }

  private def deleteEndpoint(service: EndpointService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case req@DELETE -> Root / "endpoint" / workspaceName / endpointId =>
      service.deleteEndpoint(workspaceName, endpointId).value >>= {
        case Right(endpoint: Endpoint) => Ok(endpoint.asJson)
        case Left(EndpointNotFoundError(_)) => NotFound()
      }
  }

  private def `disable/enableEndpoint`(service: EndpointService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case req@PUT -> Root / "endpoint" / workspaceName / endpointId => ???
  }

  def endpoints(service: EndpointService[F]): HttpRoutes[F] = {
    getEndpoint(service) <+> addEndpoint(service) <+> deleteEndpoint(service) <+> `disable/enableEndpoint`(service)
  }

}

object EndpointEndpoints {
  def endpoints[F[_] : Effect](service: EndpointService[F]): HttpRoutes[F] =
    new EndpointEndpoints[F]().endpoints(service)
}