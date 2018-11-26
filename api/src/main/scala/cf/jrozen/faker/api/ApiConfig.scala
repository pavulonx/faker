package cf.jrozen.faker.api

import cats.effect.Sync
import cats.implicits._
import cf.jrozen.faker.kafka.KafkaServerInfo
import cf.jrozen.faker.mongo.MongoConfig
import pureconfig.error.ConfigReaderException

case class ApiConfig(mongo: MongoConfig, kafka: KafkaServerInfo, notificationsTopic: String)

object ApiConfig {

  import pureconfig._

  def load[F[_]](implicit E: Sync[F]): F[ApiConfig] =
    E.delay(loadConfig[ApiConfig]("handler")).flatMap {
      case Right(ok) => E.pure(ok)
      case Left(e) => E.raiseError(new ConfigReaderException[ApiConfig](e))
    }
}
