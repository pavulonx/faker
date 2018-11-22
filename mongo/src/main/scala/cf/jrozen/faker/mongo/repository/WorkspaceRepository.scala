package cf.jrozen.faker.mongo.repository

import cats.Functor
import cats.effect.{Async, ContextShift}
import cf.jrozen.faker.model.{UUID, Workspace}
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import org.bson.Document


class WorkspaceRepository[F[_] : Async : Functor](col: MongoCollection[Document])(implicit cs: ContextShift[F]) extends MongoRepository[F, Workspace](col) {

  private val uuidFilter = (uuid: UUID) => Filters.eq("wsUuid", uuid)

  def findByName(name: String): F[Option[Workspace]] = {
    findBy(Filters.eq("name", name))
      .compile
      .last
  }

  def findByUuid(uuid: UUID): F[Option[Workspace]] = {
    findBy(uuidFilter(uuid))
      .compile
      .last
  }

  def deleteByUuid(uuid: UUID): F[DeleteResult] = {
    delete(uuidFilter(uuid))
  }

}

object WorkspaceRepository {
  def apply[F[_] : Async : ContextShift](col: MongoCollection[Document]): WorkspaceRepository[F] =
    new WorkspaceRepository[F](col)
}

