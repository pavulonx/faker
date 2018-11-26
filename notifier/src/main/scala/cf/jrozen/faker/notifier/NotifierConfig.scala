package cf.jrozen.faker.notifier

import cats.effect.Sync
import cats.implicits._
import cf.jrozen.faker.kafka.KafkaServerInfo
import pureconfig.error.ConfigReaderException

case class NotifierConfig(kafka: KafkaServerInfo, notificationsTopic: String)

object NotifierConfig {

  import pureconfig._

  def load[F[_]](implicit E: Sync[F]): F[NotifierConfig] =
    E.delay(loadConfig[NotifierConfig]("notifier")).flatMap {
      case Right(ok) => E.pure(ok)
      case Left(e) => E.raiseError(new ConfigReaderException[NotifierConfig](e))
    }
}
