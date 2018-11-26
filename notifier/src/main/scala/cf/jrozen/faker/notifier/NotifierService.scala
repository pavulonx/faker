package cf.jrozen.faker.notifier

import cats.effect._
import fs2.Stream
import fs2.concurrent.Topic
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.http4s.websocket.WebSocketFrame.Text

class NotifierService[F[_] : ConcurrentEffect : ContextShift : Timer](val topic: Topic[F, ConsumerRecord[String, String]]) {

  def subscribe(workspaceId: String): Stream[F, Text] = {
    topic
      .subscribe(4)
      .filter(cr => workspaceId == cr.key())
      .map(msg => Text(msg.value().toString))
  }
}

object NotifierService {

  def apply[F[_] : ConcurrentEffect : ContextShift : Timer](topic: Topic[F, ConsumerRecord[String, String]]): NotifierService[F] =
    new NotifierService[F](topic)

}