package cf.jrozen.faker.mongo.repository

import cats.Functor
import cats.effect.{Async, ContextShift}
import cf.jrozen.faker.model.domain.Endpoint
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.{Filters, Projections, Updates}
import com.mongodb.client.result.UpdateResult
import org.bson.Document


class EndpointRepository[F[_] : Async : Functor](col: MongoCollection[Document])
                                                (implicit cs: ContextShift[F]) extends MongoRepository[F, Endpoint](col) {

  def findEndpoint(workspaceName: String, endpointId: String): F[Option[Endpoint]] = {
    findBy(
      filter = Filters.and(workspaceNameFilter(workspaceName), Filters.eq("endpoints.endpointUuid", endpointId)),
      projection = Projections.elemMatch("endpoints")
    )
      .compile
      .last
  }

  def saveEndpoint(workspaceName: String, endpoint: Endpoint): F[UpdateResult] = {
    col
      .effect[F]
      .updateOne(workspaceNameFilter(workspaceName), Updates.push("endpoints", endpoint))
  }

  def deleteEndpoint(workspaceName: String, endpoint: Endpoint): F[UpdateResult] = {
    col
      .effect[F]
      .updateOne(workspaceNameFilter(workspaceName), Updates.pull("endpoints", endpoint))
  }

}

object EndpointRepository {
  def apply[F[_] : Async : ContextShift](col: MongoCollection[Document]): EndpointRepository[F] =
    new EndpointRepository[F](col)
}