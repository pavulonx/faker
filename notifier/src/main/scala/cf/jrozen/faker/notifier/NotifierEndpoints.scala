package cf.jrozen.faker.notifier

import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import cf.jrozen.faker.model.messages.Ping
import fs2.concurrent.Queue
import fs2.{Pipe, Sink, Stream}
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.Text
import org.log4s.getLogger

import scala.concurrent.duration._

class NotifierEndpoints[F[_] : Timer](implicit F: ConcurrentEffect[F]) extends Http4sDsl[F] {

  private[this] val logger = getLogger

  private def pingStream(interval: FiniteDuration): Stream[F, Text] =
    Stream.awakeEvery[F](interval).map(_ => Text(Ping("ping").asJson.toString))

  def wsDiagnostics(): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "ping" =>
      val toClient: Stream[F, WebSocketFrame] = pingStream(1 second)
      val fromClient: Sink[F, WebSocketFrame] = _.evalMap {
        case Text(t, _) => F.delay(println(t))
        case f => F.delay(println(s"Unknown type: $f"))
      }
      WebSocketBuilder[F].build(toClient, fromClient)

    case GET -> Root / "wsecho" =>
      val echoReply: Pipe[F, WebSocketFrame, WebSocketFrame] =
        _.collect {
          case Text(msg, _) => Text("Echo: " + msg)
          case _ => Text("new")
        }
      Queue
        .unbounded[F, WebSocketFrame]
        .flatMap { q =>
          val d = q.dequeue.through(echoReply)
          val e = q.enqueue
          WebSocketBuilder[F].build(d, e)
        }
  }

  def serviceEndpoint(notifierService: NotifierService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "notifications" / workspaceId =>
      val toClient: Stream[F, WebSocketFrame] = notifierService.subscribe(workspaceId) merge pingStream(10 seconds)
      val fromClient: Sink[F, WebSocketFrame] = _.evalMap(x => F.delay(logger.info(s"Received from notifications channel: $x")))

      WebSocketBuilder[F].build(toClient, fromClient)

  }

  def endpoints(notifierService: NotifierService[F]): HttpRoutes[F] = {
    wsDiagnostics <+> serviceEndpoint(notifierService)
  }
}

object NotifierEndpoints {

  def apply[F[_]](notifierService: NotifierService[F])
                 (implicit F: ConcurrentEffect[F], timer: Timer[F]): HttpRoutes[F] = {
    new NotifierEndpoints[F].endpoints(notifierService)
  }

}
