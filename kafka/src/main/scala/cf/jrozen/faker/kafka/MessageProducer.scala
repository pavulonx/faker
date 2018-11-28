package cf.jrozen.faker.kafka

import fs2.kafka.{KafkaProducer, ProducerMessage, ProducerResult}
import org.apache.kafka.clients.producer.ProducerRecord


class MessageProducer[F[_], T](topic: String)(kafkaProducer: KafkaProducer[F, String, T]) {
  def produce(key: String, message: T): F[ProducerResult[String, T, Unit]] = {
    val record: ProducerRecord[String, T] = new ProducerRecord(topic, key, message)
    kafkaProducer.produce[Unit](ProducerMessage.single(record, ()))
  }
}

object MessageProducer {
  def apply[F[_], T](topic: String)(kafkaProducer: KafkaProducer[F, String, T]): MessageProducer[F, T] =
    new MessageProducer(topic)(kafkaProducer)
}
