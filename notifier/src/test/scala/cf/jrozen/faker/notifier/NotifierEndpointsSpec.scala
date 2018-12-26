package cf.jrozen.faker.notifier

import java.time.Instant

import cats.effect._
import cf.jrozen.faker.kafka.KafkaConfiguration.emptyRecord
import cf.jrozen.faker.model.messages.{Event, Ping}
import fs2.concurrent.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.http4s._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl._
import org.http4s.implicits._
import org.scalatest._
import org.scalatest.prop.PropertyChecks

import scala.concurrent.ExecutionContext


class NotifierEndpointsSpec
  extends FunSuite
    with Matchers
    with PropertyChecks
    with Http4sDsl[IO]
    with Http4sClientDsl[IO] {

  implicit val workspace: String = "testWorkspace"

  implicit val ec: ExecutionContext = ExecutionContext.global
  implicit val contextShift: ContextShift[IO] = IO.contextShift(ec)
  implicit val timer: Timer[IO] = IO.timer(ec)

  test("connect to websocket") {

    forAll { workspaceId: String =>
      (for {
        topic <- Topic[IO, ConsumerRecord[String, IO[Event]]](emptyRecord(IO.pure(Ping("INIT", Instant.EPOCH))))
        notifierEndpoints = NotifierEndpoints[IO](NotifierService[IO](topic)).orNotFound
        request = Request[IO](GET, Uri.fromString(s"ws:///notifications/$workspaceId").right.get, headers = websocketHeaders)
        response <- notifierEndpoints.run(request)
      } yield {
        response.status shouldEqual SwitchingProtocols
      }).unsafeRunSync
    }
  }

  private val websocketHeaders = Headers(
    Header("Connection", "Upgrade"),
    Header("Sec-WebSocket-Extensions", "permessage-deflate; client_max_window_bits"),
    Header("Sec-WebSocket-Key", "IIeGzDdR/o7YFeXumHTMqA=="),
    Header("Sec-WebSocket-Version", "13"),
    Header("Upgrade", "websocket")
  )
}
