package cf.jrozen.faker.api.endpoint

import java.time.Instant
import java.util.UUID

import cats.Functor
import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._
import cf.jrozen.faker.api.EndpointNotFoundError
import cf.jrozen.faker.model.domain.Endpoint
import cf.jrozen.faker.mongo.repository.EndpointRepository

class EndpointService[F[_] : Functor](endpointRepo: EndpointRepository[F])(implicit F: Sync[F]) {

  def getEndpoints(workspaceName: String): F[List[Endpoint]] = endpointRepo.findEndpoints(workspaceName)

  def getEndpoint(workspaceName: String, endpointId: String): EitherT[F, EndpointNotFoundError, Endpoint] =
    EitherT.fromOptionF(endpointRepo.findEndpoint(workspaceName, endpointId), EndpointNotFoundError(endpointId))


  def addEndpoint(workspaceName: String, endpointRequest: EndpointRequest): F[Endpoint] = F.suspend {
    val endpoint = createEndpoint(endpointRequest)
    endpointRepo.saveEndpoint(workspaceName, endpoint).as(endpoint)
  }

  def deleteEndpoint(workspaceName: String, endpointId: String): EitherT[F, EndpointNotFoundError, Endpoint] =
    getEndpoint(workspaceName, endpointId).flatMap { endpoint: Endpoint =>
      EitherT.liftF(endpointRepo.deleteEndpoint(workspaceName, endpoint).as(endpoint))
    }

  private def createEndpoint(er: EndpointRequest) =
    Endpoint(UUID.randomUUID().toString, Instant.now, er.name, er.description, er.responseTemplate)

}

object EndpointService {
  def apply[F[_] : Functor : Sync](endpointRepo: EndpointRepository[F]): EndpointService[F] =
    new EndpointService[F](endpointRepo)
}
