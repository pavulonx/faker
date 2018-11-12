package cf.jrozen.faker.mongo

import cats.effect.{IO, Resource, Sync}
import com.mongodb.MongoClientSettings
import com.mongodb.async.client.{MongoClient, MongoClients}
import fs2._
import org.bson.Document

object MongoConnection {

//  val allDocuments: Stream[IO, Document] =
//    for {
//      conn: MongoClient <- connection[IO](MongoConfig.localDefault)
//      database = conn.getDatabase("test_db")
//      collection = database.getCollection("test_collection")
//      document <- collection.find().stream[IO]
//    } yield document

  def connection[F[_] : Sync](mongoInfo: MongoConfig): Stream[F, MongoClient] =
    Stream.resource(fromUrl[F](mongoInfo.url))

  def fromUrl[F[_]](url: String)(implicit F: Sync[F]): Resource[F, MongoClient] =
    Resource.make(F.delay(MongoClients.create(url))) { client =>
      F.delay(client.close())
    }

  def fromSettings[F[_]](settings: MongoClientSettings)(
    implicit F: Sync[F]): Resource[F, MongoClient] = {
    Resource.make(F.delay(MongoClients.create(settings)))(client =>
      F.delay(client.close()))
  }

}
