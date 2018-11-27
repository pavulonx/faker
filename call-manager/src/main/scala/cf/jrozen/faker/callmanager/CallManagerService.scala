package cf.jrozen.faker.callmanager

import cats.effect._
import cats.implicits._
import cf.jrozen.faker.model.domain.{Call, Endpoint}
import cf.jrozen.faker.model.messages.{Event, NewCall, RemoveEndpoint}
import cf.jrozen.faker.mongo.repository.CallRepositoryMutable
import org.log4s.getLogger

class CallManagerService[F[_] : ConcurrentEffect : ContextShift : Timer](callRepository: CallRepositoryMutable[F])(implicit F: Effect[F]) {

  private[this] val logger = getLogger

  def process: Event => F[Unit] = {
    case NewCall(call: Call) => callRepository.save(call).map(_ =>
      logger.info(s"Saved call: $call")
    )
    case RemoveEndpoint(endpoint: Endpoint) => callRepository.deleteByEndpointId(endpoint.endpointId).map(dr =>
      logger.info(s"Deleted ${dr.getDeletedCount} call events")
    )
    case otherwise => F.delay(
      logger.warn(s"Received unhanded message $otherwise")
    )
  }
}

object CallManagerService {

  def apply[F[_] : ConcurrentEffect : ContextShift : Timer](callRepository: CallRepositoryMutable[F]): CallManagerService[F] =
    new CallManagerService[F](callRepository)

}