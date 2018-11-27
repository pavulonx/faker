package cf.jrozen.faker.handler

import cats.effect._
import cats.implicits._
import cf.jrozen.faker.kafka.KafkaConfiguration
import cf.jrozen.faker.model.messages.Event
import fs2.Stream
import fs2.kafka.{KafkaProducer, ProducerSettings, producerStream}
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.log4s.getLogger

object HandlerApp extends IOApp {

  private[this] val logger = getLogger

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      conf <- Stream.eval(HandlerConfig.load[IO])
      _ <- Stream.eval(Sync[IO].delay(logger.info(s"Config loaded: $conf")))

      producer <- kafkaProducer[IO](conf)
      service = HandlerNotificationsService[IO](producer, conf)

      app = Router {
        "/handle" -> HandlerEndpoints.endpoints[IO](service)
        //      "/service" -> //todo: create common service mapping for diagnostics
      }.orNotFound

      exitCode <- server[IO](app)
    } yield exitCode
  }.compile.drain.as(ExitCode.Success)

  def server[F[_] : Sync : ConcurrentEffect : Timer](httpApp: HttpApp[F]): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8810)
      .withHttpApp(httpApp)
      .serve
  }

  def kafkaProducer[F[_] : ConcurrentEffect](handlerConfig: HandlerConfig): Stream[F, KafkaProducer[F, String, Event]] = {
    val kafkaProducerSettings: ProducerSettings[String, Event] =
      KafkaConfiguration.producerSettings[Event](handlerConfig.kafka).withClientId("handler")
    producerStream[F].using(kafkaProducerSettings)
  }

}
