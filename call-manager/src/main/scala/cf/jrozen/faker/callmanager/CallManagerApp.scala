package cf.jrozen.faker.callmanager

import java.time.Instant

import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import cf.jrozen.faker.kafka.KafkaConfiguration
import cf.jrozen.faker.kafka.KafkaConfiguration.emptyRecord
import cf.jrozen.faker.model.messages.{Event, Ping}
import fs2.Stream
import fs2.concurrent.Topic
import fs2.kafka.{consumerExecutionContextStream, consumerStream}
import org.apache.kafka.clients.consumer.ConsumerRecord

object CallManagerApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      conf <- Stream.eval(CallManagerConfig.load[IO])

      topic <- Stream.eval(Topic[IO, ConsumerRecord[String, IO[Event]]](emptyRecord(IO.pure(Ping("INIT", Instant.EPOCH)))))
      service = CallManagerService[IO](topic)

      server <- kafkaStream[IO](conf).to(topic.publish)
    } yield server

  }.compile.drain.as(ExitCode.Success)

  /**
    * fs2.Stream.awakeEvery[F](4 seconds).map(s => emptyRecord(Sync[F].raiseError(new Exception())))
    */
  def kafkaStream[F[_] : ConcurrentEffect : ContextShift : Timer](callManagerConfig: CallManagerConfig
                                                                 ): Stream[F, ConsumerRecord[String, F[Event]]] = for {
    executionContext <- consumerExecutionContextStream[F]
    consumerSettings = KafkaConfiguration.consumerSettings[F, Event]("call_manager", callManagerConfig.kafka)
    consumer <- consumerStream[F].using(consumerSettings(executionContext))
    _ <- consumer.subscribe(NonEmptyList.one(callManagerConfig.notificationsTopic))
    message <- consumer.stream
  } yield message.record

}
