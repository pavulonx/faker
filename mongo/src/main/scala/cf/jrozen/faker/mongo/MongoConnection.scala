package cf.jrozen.faker.mongo

import cats.effect.{Resource, Sync}
import com.mongodb.MongoClientSettings
import com.mongodb.async.client.{MongoClient, MongoClients, MongoCollection, MongoDatabase}
import fs2._
import org.bson.Document

object MongoConnection {

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

  implicit class MongoConnectionSyntax(mongoClient: MongoClient) {
    def faker: MongoDatabase = mongoClient.getDatabase("faker")
  }

  implicit class MongoDatabaseSyntax(mongoDatabase: MongoDatabase) {
    def workspaces: MongoCollection[Document] = mongoDatabase.getCollection("workspaces")

    def callEvents: MongoCollection[Document] = mongoDatabase.getCollection("calls")
  }

}
