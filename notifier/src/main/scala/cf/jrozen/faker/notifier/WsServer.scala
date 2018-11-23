package cf.jrozen.faker.notifier

import cats.effect._
import fs2._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._


object WsServer {

  def server[F[_] : Effect](notifierService: NotifierService[F])
                           (implicit cEff: ConcurrentEffect[F], cs: ContextShift[F], ec: ExecutionContext, timer: Timer[F]): Stream[F, ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(port = 8280)
      .withWebSockets(true)
      .withHttpApp(NotifierEndpoints.endpoints[F](notifierService).orNotFound)
      .withIdleTimeout(5 minutes) //todo: send ping responses by ws to avoid idle state issue
      .serve

}
