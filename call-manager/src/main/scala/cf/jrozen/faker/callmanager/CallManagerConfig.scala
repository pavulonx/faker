package cf.jrozen.faker.callmanager

import cats.effect.Sync
import cats.implicits._
import cf.jrozen.faker.kafka.KafkaServerInfo
import pureconfig.error.ConfigReaderException

case class CallManagerConfig(kafka: KafkaServerInfo, notificationsTopic: String)

object CallManagerConfig {

  import pureconfig._

  def load[F[_]](implicit E: Sync[F]): F[CallManagerConfig] =
    E.delay(loadConfig[CallManagerConfig]("callmanager")).flatMap {
      case Right(ok) => E.pure(ok)
      case Left(e) => E.raiseError(new ConfigReaderException[CallManagerConfig](e))
    }
}
