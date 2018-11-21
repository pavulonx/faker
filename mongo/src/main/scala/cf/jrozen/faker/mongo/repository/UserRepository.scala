package cf.jrozen.faker.mongo.repository

import cats.effect.{Async, ContextShift}
import cf.jrozen.faker.model.User
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.{Filters, Sorts}
import fs2.Stream
import io.circe.parser.decode
import org.bson.Document


class UserRepository[F[_] : Async](col: MongoCollection[Document])(implicit cs: ContextShift[F]) extends MongoRepository[F, User](col) {

  def getByName(name: String): Stream[F, User] = {
    col
      .find(Filters.eq("name", name))
      .sort(Sorts.descending("timestamp"))
      .limit(1)
      .stream
      .flatMap { doc: Document =>
        decode[User](doc.toJson()) match {
          case Left(_) => Stream.empty
          case Right(x) => Stream.emit(x)
        }
      }.evalTap(_ => cs.shift)
  }

}


