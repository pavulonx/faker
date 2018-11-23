package cf.jrozen.faker.api.endpoint

import java.time.Instant

import cats.Functor
import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._
import cf.jrozen.faker.api.EndpointNotFoundError
import cf.jrozen.faker.model.domain.Endpoint
import cf.jrozen.faker.mongo.repository.EndpointRepository

class EndpointService[F[_] : Functor](endpointRepo: EndpointRepository[F])(implicit F: Sync[F]) {

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
    Endpoint("", Instant.now, er.name, er.description, er.responseTemplate)

}
