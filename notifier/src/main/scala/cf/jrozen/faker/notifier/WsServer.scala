package cf.jrozen.faker.notifier

import cats.effect._
import fs2._
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext


object WsServer {

  def server[F[_] : Effect](notifierService: NotifierService[F])
                           (implicit cEff: ConcurrentEffect[F], cs: ContextShift[F], ec: ExecutionContext, timer: Timer[F]): Stream[F, ExitCode] =
    BlazeServerBuilder[F]
      .bindHttp(port = 8080)
      .withWebSockets(true)
      .withHttpApp(NotifierEndpoints.app[F](notifierService))
      .serve

}
