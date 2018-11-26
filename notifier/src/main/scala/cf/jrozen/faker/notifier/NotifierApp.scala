package cf.jrozen.faker.notifier

import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import cf.jrozen.faker.kafka.KafkaConfiguration
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

      topic <- Stream.eval(Topic[IO, ConsumerRecord[String, String]](new ConsumerRecord("", 0, 0, "START", null)))
      app = Router {
        "/" -> NotifierEndpoints.endpoints[IO](NotifierService[IO](topic))
      }.orNotFound

      server <- kafkaStream[IO](conf).to(topic.publish).concurrently(server[IO](app))
    } yield server

  }.compile.drain.as(ExitCode.Success)

  def server[F[_] : ConcurrentEffect : Timer](httpApp: HttpApp[F]): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8280)
      .withWebSockets(true)
      .withIdleTimeout(1 minute)
      .withHttpApp(httpApp)
      .serve
  }

  def kafkaStream[F[_] : ConcurrentEffect : ContextShift : Timer](notifierConfig: NotifierConfig
                                                                 ): Stream[F, ConsumerRecord[String, String]] = for {
    executionContext <- consumerExecutionContextStream[F]
    notifierGroupId = s"notifier_${Random.alphanumeric.take(5).mkString}"
    consumerSettings = KafkaConfiguration.consumerSettings[String](notifierGroupId, notifierConfig.kafka)
    consumer <- consumerStream[F].using(consumerSettings(executionContext))
    _ <- consumer.subscribe(NonEmptyList.one(notifierConfig.notificationsTopic))
    message <- consumer.stream
  } yield message.record

}
