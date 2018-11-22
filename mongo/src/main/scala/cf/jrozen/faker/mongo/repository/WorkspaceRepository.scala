package cf.jrozen.faker.mongo.repository

import cats.Functor
import cats.effect.{Async, ContextShift}
import cf.jrozen.faker.model.Workspace
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.{Filters, Sorts}
import fs2.Stream
import io.circe.parser.decode
import org.bson.Document


class WorkspaceRepository[F[_] : Async : Functor](col: MongoCollection[Document])(implicit cs: ContextShift[F]) extends MongoRepository[F, Workspace](col) {

  def findByName(name: String): F[Option[Workspace]] = {
    col
      .find(Filters.eq("name", name))
      .sort(Sorts.descending("timestamp"))
      .limit(1)
      .stream
      .flatMap { doc: Document =>
        decode[Workspace](doc.toJson()) match {
          case Left(_) => Stream.empty
          case Right(x) => Stream.emit(x)
        }
      }.evalTap(_ => cs.shift)
      .compile
      .last
  }

}

object WorkspaceRepository {
  def apply[F[_] : Async : ContextShift](col: MongoCollection[Document]): WorkspaceRepository[F] =
    new WorkspaceRepository[F](col)
}

