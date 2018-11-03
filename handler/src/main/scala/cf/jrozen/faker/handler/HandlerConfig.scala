package cf.jrozen.faker.handler

import cats.effect.Sync
import cats.implicits._
import cf.jrozen.faker.mongo.MongoConfig
import pureconfig.error.ConfigReaderException

case class HandlerConfig(mongo: MongoConfig)

object HandlerConfig {

  import pureconfig._

  def load[F[_]](implicit E: Sync[F]): F[HandlerConfig] =
    E.delay(loadConfig[HandlerConfig]("handler")).flatMap {
      case Right(ok) => E.pure(ok)
      case Left(e) => E.raiseError(new ConfigReaderException[HandlerConfig](e))
    }
}
