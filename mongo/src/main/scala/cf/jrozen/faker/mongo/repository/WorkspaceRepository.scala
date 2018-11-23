package cf.jrozen.faker.mongo.repository


import cats.Functor
import cats.effect.{Async, ContextShift}
import cf.jrozen.faker.model.domain.Workspace
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.result.DeleteResult
import org.bson.Document


class WorkspaceRepository[F[_] : Async : Functor](col: MongoCollection[Document])
                                                 (implicit cs: ContextShift[F]) extends MongoRepository[F, Workspace](col) {

  def findByName(name: String): F[Option[Workspace]] = {
    col
      .find(workspaceNameFilter(name))
      .stream
      .decode
      .evalTap(_ => cs.shift)
      .compile
      .last
  }

  def deleteByName(uuid: String): F[DeleteResult] = {
    delete(workspaceNameFilter(uuid))
  }

}

object WorkspaceRepository {
  def apply[F[_] : Async : ContextShift](col: MongoCollection[Document]): WorkspaceRepository[F] =
    new WorkspaceRepository[F](col)
}

