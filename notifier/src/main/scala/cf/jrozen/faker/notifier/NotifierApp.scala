package cf.jrozen.faker.notifier

import cats.effect._
import cf.jrozen.faker.kafka.KafkaServerInfo

object NotifierApp extends IOApp {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def run(args: List[String]): IO[ExitCode] = {

    import cats.implicits._
    val kafkaServerInfo = KafkaServerInfo("10.1.1.21")
    IO(println("Starting notifier")) *>
      IO(println(kafkaServerInfo)) *> {
      val notifierService = NotifierService[IO](kafkaServerInfo)
      WsServer.server[IO](notifierService).compile.drain.as(ExitCode.Success)
    }
  }

}
