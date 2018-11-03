package cf.jrozen.faker.mongo

import cats.effect.{IO, Sync}
import com.mongodb.async.client.MongoClient
import fs2._
import org.bson.Document
import org.lyranthe.fs2_mongodb.imports._

object MongoConnection {

  val allDocuments: Stream[IO, Document] =
    for {
      conn <- connection[IO](MongoConfig.localDefault)
      database = conn.getDatabase("test_db")
      collection = database.getCollection("test_collection")
      document <- collection.find().stream[IO]
    } yield document


  def connection[F[_] : Sync](mongoInfo: MongoConfig): Stream[F, MongoClient] =
    Stream.resource(Mongo.fromUrl[F](mongoInfo.url))

}
