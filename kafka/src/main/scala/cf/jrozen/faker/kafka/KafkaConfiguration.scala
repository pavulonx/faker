package cf.jrozen.faker.kafka

import cats.effect.Effect
import com.ovoenergy.fs2.kafka.{ConsumerSettings, _}
import com.ovoenergy.kafka.serialization.circe._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{Deserializer, Serializer, StringDeserializer}

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

object KafkaConfiguration {

  import scala.concurrent.duration._

  def configuration(groupId: String, kafkaServerInfo: KafkaServerInfo): ConsumerSettings = ConsumerSettings(
    pollTimeout = 10 seconds,
    maxParallelism = 4,
    nativeSettings = Map(
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "true",
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> kafkaServerInfo.url,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
    )
  )

  def consumeTopic[T: Decoder, F[_]: Effect](topic: String, settings: ConsumerSettings)
                                         (implicit ec: ExecutionContext): fs2.Stream[F, ConsumerRecord[String, T]] =
    consume[F](
      TopicSubscription(Set(topic)),
      new StringDeserializer,
      jsonDeserializer[T],
      settings
    )


  def jsonSerializer[T: Encoder]: Serializer[T] = circeJsonSerializer[T]

  def jsonDeserializer[T: Decoder]: Deserializer[T] = circeJsonDeserializer[T]
}
