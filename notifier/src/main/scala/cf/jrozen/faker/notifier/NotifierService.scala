package cf.jrozen.faker.notifier

import cats.effect._
import cf.jrozen.faker.model.messages.Event
import fs2.Stream
import fs2.concurrent.Topic
import io.circe.syntax._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.http4s.websocket.WebSocketFrame.Text
import org.log4s.getLogger

class NotifierService[F[_] : ConcurrentEffect : ContextShift : Timer](val topic: Topic[F, ConsumerRecord[String, F[Event]]]) {

  private[this] val logger = getLogger

  def subscribe(workspaceId: String): Stream[F, Text] = {
    topic
      .subscribe(4)
      .filter(cr => workspaceId == cr.key())
      .evalMap(m => Effect[F].handleError(m.value())(t => {
        logger.error(t)("Error occurred!")
        Event.empty
      }))
      .map(value => Text(value.asJson.toString))
  }
}

object NotifierService {

  def apply[F[_] : ConcurrentEffect : ContextShift : Timer](topic: Topic[F, ConsumerRecord[String, F[Event]]]): NotifierService[F] =
    new NotifierService[F](topic)

}