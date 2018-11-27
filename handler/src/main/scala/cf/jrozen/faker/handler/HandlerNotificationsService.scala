package cf.jrozen.faker.handler

import cats.effect._
import cf.jrozen.faker.model.domain.Call
import cf.jrozen.faker.model.messages.{Event, NewCall}
import fs2.kafka._
import org.apache.kafka.clients.producer.ProducerRecord

class HandlerNotificationsService[F[_] : Sync](producer: KafkaProducer[F, String, Event], conf: HandlerConfig) {

  def emit(call: Call): F[ProducerResult[String, Event, Unit]] = producer.produce(callEvent(call))

  private def callEvent(call: Call): ProducerMessage[String, Event, Unit] = {
    val record: ProducerRecord[String, Event] = new ProducerRecord(conf.notificationsTopic, call.workspaceName, NewCall(call))
    ProducerMessage.single(record, ())
  }

}

object HandlerNotificationsService {
  def apply[F[_] : Sync](producer: KafkaProducer[F, String, Event], conf: HandlerConfig): HandlerNotificationsService[F] = {
    new HandlerNotificationsService[F](producer, conf)
  }
}
