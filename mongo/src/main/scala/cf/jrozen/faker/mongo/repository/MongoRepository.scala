package cf.jrozen.faker.mongo.repository

import java.util.ConcurrentModificationException

import cats.MonadError
import cats.data.NonEmptyList
import cats.effect.{Async, ContextShift}
import cats.implicits._
import cf.jrozen.faker.mongo.MongoCirce._
import cf.jrozen.faker.mongo.MongoFs2._
import com.mongodb.MongoBulkWriteException
import com.mongodb.async.client.MongoCollection
import com.mongodb.client.model.Filters
import fs2.Stream
import io.circe.parser._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.bson.Document
import org.bson.types.ObjectId

import scala.collection.JavaConverters._

class MongoRepository[F[_] : Async, T](col: MongoCollection[Document])
                                      (implicit cs: ContextShift[F], val decoder: Decoder[T], val encoder: Encoder[T]) {

  def getOne(_id: String): Stream[F, T] = {
    col
      .find(Filters.eq("_id", _id))
      .stream
      .flatMap { doc: Document =>
        decode[T](doc.toJson()) match {
          case Left(_) => Stream.empty
          case Right(x) => Stream.emit(x)
        }
      }.evalTap(_ => cs.shift)
  }

  def saveMany(entities: NonEmptyList[T]): F[Unit] = {
    val documents: Seq[(ObjectId, Document)] = entities.map { event: T =>
      val id = ObjectId.get()
      (id, event.asJson.toDocument(id))
    }.toList

    val insertion: F[Unit] = col.effect[F].insertMany(documents.map(_._2)).attempt.flatMap {
      case Right(_) => ().pure[F]
      case Left(ex: MongoBulkWriteException) =>
        val insertedDocs = Filters.in("_id", documents.map(_._1).take(ex.getWriteResult.getInsertedCount).asJava)
        col.effect[F].deleteMany(insertedDocs) *> MonadError[F, Throwable].raiseError(new ConcurrentModificationException(entities.toString))
      case Left(ex) => MonadError[F, Throwable].raiseError(ex)
    }

    insertion.attempt.flatTap(_ => cs.shift).rethrow
  }

  def save(entity: T): F[Unit] = saveMany(NonEmptyList.one(entity))

}