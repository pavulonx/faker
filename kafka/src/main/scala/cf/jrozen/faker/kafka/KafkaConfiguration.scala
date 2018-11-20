package cf.jrozen.faker.kafka

import com.ovoenergy.kafka.serialization.circe._
import fs2.kafka._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer, StringSerializer}

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

object KafkaConfiguration {

  import scala.concurrent.duration._

  def consumerSettings[V: Decoder](groupId: String, kafkaServerInfo: KafkaServerInfo)
  : ExecutionContext => ConsumerSettings[String, V] =
    (ec: ExecutionContext) =>
      ConsumerSettings[String, V](new StringDeserializer, jsonDeserializer[V], ec)
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers(kafkaServerInfo.url)
        .withPollTimeout(250 milliseconds)
        .withGroupId(groupId)
        .withEnableAutoCommit(true)


  def producerSettings[V: Encoder](kafkaServerInfo: KafkaServerInfo): ProducerSettings[String, V] =
    ProducerSettings(keySerializer = new StringSerializer, valueSerializer = jsonSerializer[V])
      .withBootstrapServers(kafkaServerInfo.url)


  private def jsonSerializer[T: Encoder]: Serializer[T] = circeJsonSerializer[T]

  private def jsonDeserializer[T: Decoder]: Deserializer[T] = circeJsonDeserializer[T]
}
