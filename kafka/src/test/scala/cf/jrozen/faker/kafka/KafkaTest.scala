package cf.jrozen.faker.kafka

import java.time.Instant

import cats.data.NonEmptyList
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.traverse._
import fs2.kafka._
import io.circe.Decoder.Result
import io.circe._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

import scala.concurrent.duration._

case class TestMsg(msg: String, timestamp: Long = Instant.now.toEpochMilli)


object TestMsg {

  implicit val encoder: Encoder[TestMsg] = new Encoder[TestMsg] {
    override def apply(a: TestMsg): Json = Json.obj(
      ("msg", Json.fromString(a.msg)),
      ("timestamp", Json.fromLong(a.timestamp))
    )
  }

  implicit val decoder: Decoder[TestMsg] = new Decoder[TestMsg] {
    override def apply(c: HCursor): Result[TestMsg] = Right(TestMsg(c.get[String]("msg").right.get, c.get[Long]("msg").right.get))
  }
}


object KafkaTest extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {


//    consumerExecutionContextStream

//    val consumerSettings = KafkaConfiguration.consumerSettings[TestMsg]("testId")
//
//    val producerSettings = KafkaConfiguration.producerSettings[TestMsg]()
//
//    val topics =
//      NonEmptyList.one("topicNIO1HeadStage")
//
//    def processRecord(record: ConsumerRecord[String, TestMsg]): IO[(String, TestMsg)] =
//      IO.pure(record.key -> record.value)

//    val stream =
//      for {
//        executionContext <- consumerExecutionContextStream[IO]
//        consumer <- consumerStream[IO].using(consumerSettings(executionContext))
//        producer <- producerStream[IO].using(producerSettings)
//        _ <- consumer.subscribe(topics)
//        _ <- consumer.stream
//          .mapAsync(25)(message =>
//            processRecord(message.record)
//              .map {
//                case (key: String, value: TestMsg) =>
//                  val record = new ProducerRecord("topic", key, value)
//                  ProducerMessage.single(record, message.committableOffset)
//              })
//          .evalMap(producer.produceBatched)
//          .groupWithin(500, 15.seconds)
//          .evalMap {
//            _.traverse(_.map(_.passthrough))
//              .map(_.foldLeft(CommittableOffsetBatch.empty[IO])(_ updated _))
//              .flatMap(_.commit)
//          }
//      } yield ()



//    val topic = "my-topic"
//    val value = TestMsg("dupa1")
//    val key = s"testmsg_${value.hashCode.toString}"

    val producerSettings = KafkaConfiguration.producerSettings[String](KafkaServerInfo("172.22.0.2"))
      .withMaxInFlightRequestsPerConnection(1)
      .withAcks(Acks.All)
      .withClientId("KafkaExampleProducer")


//    val topic = "my-topic"
//    val key = "my-key"
//    val value = "my-value"
//case class Ping(msg: String, timestamp: Long = Instant.now.toEpochMilli)

//        val topic = "events.test"
        val topic = "notifications"
//        val value = TestMsg("dupa1")
        val value = "{\n  \"msg\": \"somemsg\",\n  \"timestamp\": 21372137\n}"
        val key = s"testmsg_${value.hashCode.toString}"

    producerResource[IO]
      .using(producerSettings)
      .use { producer =>
        val record = new ProducerRecord(topic, key, value)
        val message = ProducerMessage.single(record, ())
        producer.produce(message)
      }
      .map(_ => ExitCode(0))
  }
}