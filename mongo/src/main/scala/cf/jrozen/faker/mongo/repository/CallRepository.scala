package cf.jrozen.faker.mongo.repository

import cats.effect.{Async, ContextShift}
import cf.jrozen.faker.model.domain.Call
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.{Filters, Sorts}
import org.bson.Document


trait CallRepository[F[_]] {
  def find(workspaceName: String, endpointId: String): F[List[Call]]
}

trait CallRepositoryMutable[F[_]] extends CallRepository[F] {
  def save(call: Call): F[Unit]
}

private[repository] sealed class CallRepositoryImpl[F[_] : Async](col: MongoCollection[Document])
                                                                 (implicit cs: ContextShift[F])
  extends MongoRepository[F, Call](col)
    with CallRepository[F]
    with CallRepositoryMutable[F] {

  override def find(workspaceName: String, endpointId: String): F[List[Call]] = {
    col
      .find(Filters.and(
        Filters.eq("workspaceName", workspaceName),
        Filters.eq("endpointId", endpointId)))
      .sort(Sorts.descending("timestamp"))
      .stream
      .decode
      .compile
      .toList
  }
}

object CallRepository {


  def apply[F[_] : Async : ContextShift](col: MongoCollection[Document]): CallRepository[F] = new CallRepositoryImpl[F](col)

  def mutable[F[_] : Async : ContextShift](col: MongoCollection[Document]): CallRepositoryMutable[F] = new CallRepositoryImpl[F](col)

}