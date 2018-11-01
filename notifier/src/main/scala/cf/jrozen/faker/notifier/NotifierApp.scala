package cf.jrozen.faker.notifier

import cats.effect.{ExitCode, IO, IOApp}
import cf.jrozen.faker.kafka.KafkaServerInfo

import scala.language.higherKinds

object NotifierApp extends IOApp {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def run(args: List[String]): IO[ExitCode] = {

    import cats.implicits._
    val kafkaServerInfo = KafkaServerInfo("172.22.0.2")
    IO(println("Starting notifier")) *>
      IO(println(kafkaServerInfo)) *> {
      val notifierService = new NotifierService[IO](kafkaServerInfo)
      WsServer.server[IO](notifierService).compile.drain.as(ExitCode.Success)
    }
  }

}
