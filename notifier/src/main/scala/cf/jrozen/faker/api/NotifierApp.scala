package cf.jrozen.faker.api

import cats.effect.IO
import fs2.StreamApp

import scala.language.higherKinds

object NotifierApp extends StreamApp[IO] {

  import scala.concurrent.ExecutionContext.Implicits.global

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = WsServer.server[IO]


}
