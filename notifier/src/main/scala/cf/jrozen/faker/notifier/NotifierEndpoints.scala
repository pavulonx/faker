package cf.jrozen.faker.notifier

import cats.data.Kleisli
import cats.effect.{ConcurrentEffect, Effect, Sync, Timer}
import cats.implicits._
import fs2.concurrent.Queue
import fs2.{Pipe, Sink, Stream}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.websocket.WebSocketBuilder
import org.http4s.websocket.WebSocketFrame
import org.http4s.websocket.WebSocketFrame.{Close, Text}
import org.http4s.{HttpApp, HttpRoutes, Response}

import scala.concurrent.duration._

class NotifierEndpoints[F[_] : Effect](implicit F: ConcurrentEffect[F], timer: Timer[F]) extends Http4sDsl[F] {

  private def pingStream(interval: FiniteDuration = 10 seconds): Stream[F, Text] =
    Stream.awakeEvery[F](interval).map(d => Text(s"Ping! $d"))

  def serviceInfo(): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "info" =>
      Ok("faker-notifier")

    case GET -> Root / "version" =>
      Ok("0.0.1")
  }

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
    case GET -> Root / "notifications" / clientId =>
      val toClient: Stream[F, WebSocketFrame] = notifierService.subscribe(clientId) merge pingStream()
      val fromClient: Sink[F, WebSocketFrame] = _.evalMap { // todo: commit msg - now autocommit
        case Text(t: String, _) => F.delay(println(s"Client msg: " + t))
        case Close(_) => F.delay(println(s"Client $clientId connection close"))
        case _ => F.delay(())
      }
      WebSocketBuilder[F].build(toClient, fromClient)

  }

  def endpoints(notifierService: NotifierService[F]): HttpRoutes[F] = {
    serviceInfo <+> wsDiagnostics <+> serviceEndpoint(notifierService)
  }
}

object NotifierEndpoints {

  def endpoints[F[_] : Effect](notifierService: NotifierService[F])
                              (implicit F: ConcurrentEffect[F], timer: Timer[F]): HttpRoutes[F] = {

    new NotifierEndpoints[F].endpoints(notifierService)
  }

  def app[F[_] : Effect](notifierService: NotifierService[F])
                        (implicit S: Sync[F], F: ConcurrentEffect[F], timer: Timer[F]): HttpApp[F] = {
    Kleisli(a => endpoints(notifierService).run(a).getOrElse(Response.notFound))
  }
}
