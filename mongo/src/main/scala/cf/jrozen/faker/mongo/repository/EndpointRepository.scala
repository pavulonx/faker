package cf.jrozen.faker.mongo.repository

import cats.effect.{ContextShift, Effect}
import cf.jrozen.faker.model.Endpoint
import com.mongodb.async.client.MongoCollection
import org.bson.Document

class EndpointRepository[F[_] : Effect : ContextShift](col: MongoCollection[Document]) extends MongoRepository[F, Endpoint](col) {
}

object EndpointRepository {
}
