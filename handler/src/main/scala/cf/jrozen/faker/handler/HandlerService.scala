package cf.jrozen.faker.handler

import cats.effect._
import cf.jrozen.faker.kafka.KafkaConfiguration
import fs2.kafka.{KafkaProducer, ProducerMessage, ProducerSettings, producerResource}
import org.apache.kafka.clients.producer.ProducerRecord

class HandlerService[F[_]: ConcurrentEffect: ContextShift: Sync](handlerConfig: HandlerConfig) {

  val kafkaProducer: ProducerSettings[String, String] = KafkaConfiguration.producerSettings[String](handlerConfig.kafka).withClientId("handler")
  val topic = "notifications"
//  val value = "{\n  \"msg\": \"somemsg\",\n  \"timestamp\": 21372137\n}"
//  val key = s"testmsg_${producingRes.hashCode.toString}"

  private val producingRes: Resource[F, KafkaProducer[F, String, String]] = producerResource[F]
    .using(kafkaProducer)



  // !!!!!!!! bad
  def emit(value: String) = producingRes
    .use { producer =>
      val record = new ProducerRecord(topic, "dupa", value)
      val message: ProducerMessage[String, String, Unit] = ProducerMessage.single(record, ())
      producer.produce(message)
    }




}

object HandlerService {
  def apply[F[_]: ConcurrentEffect: ContextShift](handlerConfig: HandlerConfig): HandlerService[F] =
    new HandlerService[F](handlerConfig)
}
