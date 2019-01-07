package cf.jrozen.faker.notifier

import java.time.Instant

import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import cf.jrozen.faker.commons.web.{ServiceInfo, ServiceInfoEndpoints}
import cf.jrozen.faker.kafka.KafkaConfiguration
import cf.jrozen.faker.kafka.KafkaConfiguration.emptyRecord
import cf.jrozen.faker.model.messages.{Event, Ping}
import fs2.Stream
import fs2.concurrent.Topic
import fs2.kafka.{consumerExecutionContextStream, consumerStream}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.duration._
import scala.util.Random

object NotifierApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      conf <- Stream.eval(NotifierConfig.load[IO])

      topic <- Stream.eval(Topic[IO, ConsumerRecord[String, IO[Event]]](emptyRecord(IO.pure(Ping("INIT", Instant.EPOCH)))))
      app = Router(
        "/" -> NotifierEndpoints[IO](NotifierService[IO](topic)),
        "/service" -> ServiceInfoEndpoints[IO](ServiceInfo("notifier"))
      ).orNotFound

      server <- kafkaStream[IO](conf).to(topic.publish).concurrently(server[IO](app))
    } yield server

  }.compile.drain.as(ExitCode.Success)

  def server[F[_] : ConcurrentEffect : Timer](httpApp: HttpApp[F]): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8822)
      .withWebSockets(true)
      .withIdleTimeout(1 minute)
      .withHttpApp(httpApp)
      .serve
  }

  /**
    * fs2.Stream.awakeEvery[F](4 seconds).map(s => emptyRecord(Sync[F].raiseError(new Exception())))
    */
  def kafkaStream[F[_] : ConcurrentEffect : ContextShift : Timer](notifierConfig: NotifierConfig
                                                                 ): Stream[F, ConsumerRecord[String, F[Event]]] = for {
    executionContext <- consumerExecutionContextStream[F]
    notifierGroupId = s"notifier_${Random.alphanumeric.take(5).mkString}"
    consumerSettings = KafkaConfiguration.consumerSettings[F, Event](notifierGroupId, notifierConfig.kafka)
    consumer <- consumerStream[F].using(consumerSettings(executionContext))
    _ <- Stream.eval(consumer.subscribe(NonEmptyList.one(notifierConfig.notificationsTopic)))
    message <- consumer.stream
  } yield message.record

}
