package cf.jrozen.faker.mongo

import io.circe.Json
import org.bson.Document
import org.bson.types.ObjectId

object MongoCirce {

  implicit class JsonMongoSyntax(underlying: Json) {
    def toDocument(id: ObjectId): Document =
      Document.parse(underlying.toString)
        .append("_id", id)
  }

}
