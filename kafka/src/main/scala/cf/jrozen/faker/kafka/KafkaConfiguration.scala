package cf.jrozen.faker.kafka

import java.nio.charset.StandardCharsets

import cats.ApplicativeError
import com.ovoenergy.kafka.serialization.core._
import fs2.kafka._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer, Deserializer => KafkaDeserializer, Serializer => KafkaSerializer}

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

object KafkaConfiguration {

  import scala.concurrent.duration._

  def consumerSettings[F[_], V: Decoder](groupId: String, kafkaServerInfo: KafkaServerInfo)(implicit F: ApplicativeError[F, Throwable])
  : ExecutionContext => ConsumerSettings[String, F[V]] =
    (ec: ExecutionContext) =>
      ConsumerSettings[String, F[V]](new StringDeserializer, jsonDeserializer[F, V], ec)
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers(kafkaServerInfo.url)
        .withPollTimeout(250 milliseconds)
        .withGroupId(groupId)
        .withEnableAutoCommit(true)


  def producerSettings[V: Encoder](kafkaServerInfo: KafkaServerInfo): ProducerSettings[String, V] =
    ProducerSettings(keySerializer = new StringSerializer, valueSerializer = jsonSerializer[V])
      .withBootstrapServers(kafkaServerInfo.url)


  private def jsonSerializer[T: Encoder]: KafkaSerializer[T] = circeJsonSerializer[T]

  private def jsonDeserializer[F[_], T: Decoder](implicit F: ApplicativeError[F, Throwable]): KafkaDeserializer[F[T]] = circeJsonDeserializer[F, T]

  private def circeJsonSerializer[T: Encoder]: KafkaSerializer[T] = serializer { (_, data) =>
    data.asJson.noSpaces.getBytes(StandardCharsets.UTF_8)
  }

  private def circeJsonDeserializer[F[_], T: Decoder](implicit F: ApplicativeError[F, Throwable]): KafkaDeserializer[F[T]] = deserializer { (_, data) =>
    parse(new String(data, StandardCharsets.UTF_8)).flatMap(_.as[T]).fold(e => F.raiseError(e), F.pure)
  }

  def emptyRecord[T](value: T): ConsumerRecord[String, T] =
    new ConsumerRecord("", 0, 0, "", value)

}
