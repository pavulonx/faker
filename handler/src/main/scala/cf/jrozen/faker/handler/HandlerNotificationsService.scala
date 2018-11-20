package cf.jrozen.faker.handler

import cats.effect._
import cats.{FlatMap, Functor}
import cf.jrozen.faker.kafka.KafkaConfiguration
import fs2.kafka._
import org.apache.kafka.clients.producer.ProducerRecord

class HandlerNotificationsService[F[_] : ConcurrentEffect : ContextShift : Sync : ContextShift : Functor : Timer : FlatMap](handlerConfig: HandlerConfig) {

  private def kafkaProducerSettings: ProducerSettings[String, String] = KafkaConfiguration.producerSettings[String](handlerConfig.kafka).withClientId("handler")

  private def producingRes: Resource[F, KafkaProducer[F, String, String]] = producerResource[F]
    .using(kafkaProducerSettings)


  def emit(value: String): F[ProducerResult[String, String, Unit]] = {
    val stream =
      for {
        producer <- producerStream[F].using(kafkaProducerSettings)
        result <- fs2.Stream.eval(producer.produce(msg(value)))
      } yield result
    stream.compile.lastOrError
  }

  private def msg(value: String) = {
    val record = new ProducerRecord(handlerConfig.notificationsTopic, value.hashCode.toString, value)
    ProducerMessage.single(record, ())
  }

}

object HandlerNotificationsService {
  def apply[F[_] : ConcurrentEffect : ContextShift : Timer](handlerConfig: HandlerConfig): HandlerNotificationsService[F] = {
    new HandlerNotificationsService[F](handlerConfig)
  }
}
