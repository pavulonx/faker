package cf.jrozen.faker.mongo

import io.circe.syntax._
import io.circe.{Encoder, Json}
import org.bson.Document
import org.bson.types.ObjectId

object MongoCirce {

  implicit class JsonMongoSyntax(underlying: Json) {
    def toDocument(id: ObjectId): Document =
      Document.parse(underlying.toString)
        .append("_id", id)
  }

  implicit class EntityMongoSyntax[T: Encoder](underlying: T) {
    def asDocument: Document =
      Document.parse(underlying.asJson.toString)
  }

}
