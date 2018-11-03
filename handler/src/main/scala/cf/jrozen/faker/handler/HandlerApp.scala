package cf.jrozen.faker.handler

import cats.effect._
import cats.implicits._
import fs2.Stream
import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

object HandlerApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    serverStream[IO].compile.drain.as(ExitCode.Success)
  }


  def serverStream[F[_] : Effect]: Stream[F, ExitCode] = {
    for {
      configs <- Stream.eval(HandlerConfig.load[F])
    } yield exitCode
  }

  def server[F[_]] = {
    BlazeServerBuilder[F]
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
  }

  def app[F[_]]: HttpApp[F] = {
    Router {
      "/handle" -> HandlerEndpoints.endpoints[F]
      //      "/service" -> //todo: create common service mapping for diagnostics
    }
  }
}
