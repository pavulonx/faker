package cf.jrozen.faker.notifier

import cats.data.NonEmptyList
import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cf.jrozen.faker.kafka.{KafkaConfiguration, KafkaServerInfo}
import fs2.Stream
import fs2.kafka._
import io.circe.Json
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.Text

import scala.concurrent.ExecutionContext

class NotifierService[F[_] : ConcurrentEffect : ContextShift : Timer](val kafkaServerInfo: KafkaServerInfo) {

  def subscribe(clientId: String): Stream[F, WebSocketFrame] = {

    val consumerSettings = KafkaConfiguration.consumerSettings[Json](s"notifier_$clientId", kafkaServerInfo)
    (for {
      executionContext <- consumerExecutionContextStream[F]
      consumer <- consumerStream[F].using(consumerSettings(executionContext))
      _ <- consumer.subscribe(NonEmptyList.one("notifications"))
      message <- consumer.stream
    } yield message.record)
      .map(msg => Text(msg.value().toString))
  }
}

object NotifierService {

  def apply[F[_]](kafkaServerInfo: KafkaServerInfo)(
    implicit F: ConcurrentEffect[F], cs: ContextShift[F], ec: ExecutionContext, timer: Timer[F]
  ): NotifierService[F] = new NotifierService[F](kafkaServerInfo)

}