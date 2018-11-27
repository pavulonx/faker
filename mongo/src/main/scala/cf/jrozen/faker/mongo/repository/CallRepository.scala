package cf.jrozen.faker.mongo.repository

import cats.effect.{Async, ContextShift}
import cf.jrozen.faker.model.domain.Call
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.{Filters, Sorts}
import com.mongodb.client.result.DeleteResult
import org.bson.Document
import org.bson.conversions.Bson


trait CallRepository[F[_]] {
  def find(workspaceName: String, endpointId: String): F[List[Call]]
}

trait CallRepositoryMutable[F[_]] extends CallRepository[F] {
  def save(call: Call): F[Unit]

  def delete(call: Call): F[DeleteResult]

  def deleteByEndpointId(endpointId: String): F[DeleteResult]
}

private[repository] sealed class CallRepositoryImpl[F[_] : Async](col: MongoCollection[Document])
                                                                 (implicit cs: ContextShift[F])
  extends MongoRepository[F, Call](col)
    with CallRepository[F]
    with CallRepositoryMutable[F] {

  def callFilter(workspaceName: String, endpointId: String): Bson = Filters.and(
    Filters.eq("workspaceName", workspaceName),
    Filters.eq("endpointId", endpointId))

  override def find(workspaceName: String, endpointId: String): F[List[Call]] = {
    col
      .find(callFilter(workspaceName, endpointId))
      .sort(Sorts.descending("timestamp"))
      .stream
      .decode
      .compile
      .toList
  }

  override def delete(call: Call): F[DeleteResult] =
    col
      .effect[F]
      .deleteMany(callFilter(call.workspaceName, call.endpointId))

  override def deleteByEndpointId(endpointId: String): F[DeleteResult] = {
    delete(Filters.eq("endpointId", endpointId))
  }
}

object CallRepository {


  def apply[F[_] : Async : ContextShift](col: MongoCollection[Document]): CallRepository[F] = new CallRepositoryImpl[F](col)

  def mutable[F[_] : Async : ContextShift](col: MongoCollection[Document]): CallRepositoryMutable[F] = new CallRepositoryImpl[F](col)

}