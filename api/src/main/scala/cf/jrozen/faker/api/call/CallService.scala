package cf.jrozen.faker.api.call

import cf.jrozen.faker.model.domain.Call
import cf.jrozen.faker.mongo.repository.CallRepository

class CallService[F[_]](callsRepo: CallRepository[F]) {

  def findCalls(workspaceName: String, endpointId: String): F[List[Call]] =
    callsRepo.find(workspaceName, endpointId)

}

object CallService {
  def apply[F[_]](callsRepo: CallRepository[F]): CallService[F] = new CallService[F](callsRepo)
}

