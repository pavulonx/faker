package cf.jrozen.faker.notifier

import cats.effect.{Sync, _}
import cats.implicits._
import fs2.Stream
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.duration._

object NotifierApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      conf <- NotifierConfig.load[IO]

      app = Router {
        "/" -> NotifierEndpoints.endpoints[IO](NotifierService[IO](conf))
      }.orNotFound

      server <- server[IO](app).compile.drain.as(ExitCode.Success)
    } yield server
  }

  def server[F[_] : Sync : ConcurrentEffect : Timer](httpApp: HttpApp[F]): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8280)
      .withWebSockets(true)
      .withIdleTimeout(1 minute) //todo: send ping responses by ws to avoid idle state issue
      .withHttpApp(httpApp)
      .serve
  }
}
