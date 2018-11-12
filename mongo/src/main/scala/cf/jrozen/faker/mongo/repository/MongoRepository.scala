package cf.jrozen.faker.mongo.repository

import java.util.ConcurrentModificationException

import cats.MonadError
import cats.data.NonEmptyList
import cats.effect.{Async, ContextShift}
import cats.implicits._
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.MongoBulkWriteException
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.{Filters, Sorts}
import fs2.Stream
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.bson.Document
import org.bson.types.ObjectId

import scala.collection.JavaConverters._

class MongoRepository[F[_] : Async, T: Decoder : Encoder](col: MongoCollection[Document])(implicit cs: ContextShift[F]) {

  def getOne(uuid: String): Stream[F, T] = {
    col
      .find(Filters.eq("uuid", uuid))
      .sort(Sorts.descending("timestamp")).stream
      .flatMap { doc: Document =>
        decode[T](doc.toJson()) match {
          case Left(_) => Stream.empty
          case Right(x) => Stream.emit(x)
        }
      }.evalTap(_ => cs.shift)
  }

  def saveMany(events: NonEmptyList[T]): F[Unit] = {
    val documents = events.map { event: T =>
      val id = ObjectId.get()
      (id, Document.parse(event.asJson.toString()))
    }.toList

    val insertion: F[Unit] = col.effect[F].insertMany(documents.map(_._2)).attempt.flatMap {
      case Right(_) => ().pure[F]
      case Left(ex: MongoBulkWriteException) =>
        val insertedDocs = Filters.in("uuid", documents.map(_._2.getString("uuid")).take(ex.getWriteResult.getInsertedCount).asJava)
        col.effect[F].deleteMany(insertedDocs) *> MonadError[F, Throwable].raiseError(new ConcurrentModificationException(documents.head.toString()))
      case Left(ex) => MonadError[F, Throwable].raiseError(ex)
    }

    insertion.attempt.flatTap(_ => cs.shift).rethrow
  }
}
