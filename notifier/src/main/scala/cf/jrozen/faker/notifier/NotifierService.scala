package cf.jrozen.faker.notifier

import cats.data.NonEmptyList
import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cf.jrozen.faker.kafka.KafkaConfiguration
import fs2.Stream
import fs2.kafka._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.http4s.websocket.WebSocketFrame.Text

import scala.util.Random

class NotifierService[F[_] : ConcurrentEffect : ContextShift : Timer](val notifierConfig: NotifierConfig) {

  def kafkaStream(workspaceId: String): Stream[F, ConsumerRecord[String, String]] = { //todo: one consumer per app
    {
      for {
        executionContext <- consumerExecutionContextStream[F]
        consumerSettings = KafkaConfiguration.consumerSettings[String](s"notifier_${Random.alphanumeric.take(5).mkString}", notifierConfig.kafka)
        consumer <- consumerStream[F].using(consumerSettings(executionContext))
        _ <- consumer.subscribe(NonEmptyList.one(notifierConfig.notificationsTopic))
        message <- consumer.stream
      } yield message.record
    }
  }

  def subscribe(workspaceId: String) = {
    kafkaStream(workspaceId).map(msg => Text(msg.value().toString))
  }
}

object NotifierService {

  def apply[F[_] : ConcurrentEffect : ContextShift : Timer](notifierConfig: NotifierConfig): NotifierService[F] =
    new NotifierService[F](notifierConfig)

}