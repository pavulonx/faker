package cf.jrozen.faker.notifier

import cats.data.NonEmptyList
import cats.effect.{ConcurrentEffect, ContextShift, Effect, Timer}
import cf.jrozen.faker.kafka.{KafkaConfiguration, KafkaServerInfo}
import fs2.Stream
import fs2.kafka._
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.Text

import scala.concurrent.ExecutionContext

class NotifierService[F[_] : Effect](val kafkaServerInfo: KafkaServerInfo)
                                    (implicit F: ConcurrentEffect[F], cs: ContextShift[F], ec: ExecutionContext, timer: Timer[F]) {

  def subscribe(clientId: String): Stream[F, WebSocketFrame] = {

    //        val consumerSettings = KafkaConfiguration.consumerSettings[Ping](s"notifier_$clientId", kafkaServerInfo)
    val consumerSettings = KafkaConfiguration.consumerSettings[String](s"notifier_$clientId", kafkaServerInfo)

    {
      for {
        executionContext <- consumerExecutionContextStream[F]
        consumer <- consumerStream[F].using(consumerSettings(executionContext))
        _ <- consumer.subscribe(NonEmptyList.one("notifications"))
        message <- consumer.stream
      } yield message.record
    }.map {

      //                        implicitly[Encoder[Ping]].
      _.value()
      //        p => p.asJson
    }.map(
      //            msg => Text(Ping.encoder.apply(msg).asString.getOrElse("Error"))
      msg => Text(msg)
    )

    //    Stream.random.take(1000).map(_.toString).map(Text(_, last = false))
    //        Stream.random.take(1000).map(_.toString).map("prepend_"+_+"_appendix").map(Text(_))
    //    Stream.awakeEvery[F](1.seconds).map(d => Text(s"Ping! $d"))
  }

}

object NotifierService {

  def apply[F[_]](kafkaServerInfo: KafkaServerInfo)(
    implicit F: ConcurrentEffect[F], cs: ContextShift[F], ec: ExecutionContext, timer: Timer[F]
  ): NotifierService[F] = new NotifierService[F](kafkaServerInfo)

}