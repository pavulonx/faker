package cf.jrozen.faker.mongo.repository


import cats.Functor
import cats.effect.{Async, ContextShift}
import cats.implicits._
import cf.jrozen.faker.model.{UUID, Workspace}
import cf.jrozen.faker.mongo.MongoCirce._
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import io.circe.syntax._
import org.bson.Document


class WorkspaceRepository[F[_] : Async : Functor](col: MongoCollection[Document])(implicit cs: ContextShift[F]) extends MongoRepository[F, Workspace](col) {

  private val nameFilter = (uuid: UUID) => Filters.eq("wsUuid", uuid)

  def findByName(name: String): F[Option[Workspace]] = {
    findBy(nameFilter(name))
      .compile
      .last
  }

  def deleteByName(uuid: UUID): F[DeleteResult] = {
    delete(nameFilter(uuid))
  }

  def update(workspace: Workspace) = {
    col.effect[F]
      .replaceOne(nameFilter(workspace.name), workspace.asJson.toDocument)
      .flatTap(_ => cs.shift)
  }
}

object WorkspaceRepository {
  def apply[F[_] : Async : ContextShift](col: MongoCollection[Document]): WorkspaceRepository[F] =
    new WorkspaceRepository[F](col)
}

