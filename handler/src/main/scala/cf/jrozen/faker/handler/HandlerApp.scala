package cf.jrozen.faker.handler

import cats.effect._
import cats.implicits._
import fs2.Stream
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.log4s.getLogger

object HandlerApp extends IOApp {

  private[this] val logger = getLogger

  override def run(args: List[String]): IO[ExitCode] = {
    serverStream[IO].compile.drain.as(ExitCode.Success)
  }

  def serverStream[F[_] : Effect : Sync : ConcurrentEffect : Timer : ContextShift]: Stream[F, ExitCode] = {
    for {
      configs <- Stream.eval(HandlerConfig.load[F])
      _ <- Stream.eval(Sync[F].delay(logger.info(s"Config loaded: $configs")))
      service = HandlerNotificationsService[F](configs)
      app = createApp[F](service)
      exitCode <- server(app)
    } yield exitCode
  }

  def server[F[_] : Sync : ConcurrentEffect : Timer](httpApp: HttpApp[F]): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8810, "localhost")
      .withHttpApp(httpApp)
      .serve
  }

  def createApp[F[_] : Sync : Timer](handlerService: HandlerNotificationsService[F]) = {
    Router {
      "/endpoint" -> HandlerEndpoints.endpoints[F](handlerService)
      //      "/service" -> //todo: create common service mapping for diagnostics
    }.orNotFound
  }
}
