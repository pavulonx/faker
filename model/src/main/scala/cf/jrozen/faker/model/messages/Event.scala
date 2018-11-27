package cf.jrozen.faker.model.messages

import java.time.Instant

import cf.jrozen.faker.model.domain.{Call, Endpoint}
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._

import scala.reflect.ClassTag


// fixme
object Event {

  private case object Empty extends Event

  def empty: Event = Empty

  implicit val eventEncoder: Encoder[Event] = Encoder.instance {
    case Empty => JsonObject.empty.asJson
    case p@Ping(_, _) => p.asJson
    case n@NewCall(_) => n.asJson
    case r@RemoveEndpoint(_) => r.asJson
  }

  implicit val eventDecoder: Decoder[Event] = for {
    visitorType <- Decoder[String].prepare(_.downField("entityType"))
    value <- visitorType match {
      case "Ping" => Decoder[Ping]
      case "NewCall" => Decoder[NewCall]
      case "RemoveEndpoint" => Decoder[RemoveEndpoint]
      case other => Decoder.failedWithMessage(s"invalid type: $other")
    }
  } yield value

}

sealed trait Event

case class NewCall(call: Call) extends Event

object NewCall {
  implicit val encoder: Encoder[NewCall] = TypedEncoder(deriveEncoder[NewCall])
  implicit val decoder: Decoder[NewCall] = deriveDecoder
}

case class Ping(msg: String, timestamp: Instant = Instant.now) extends Event

object Ping {
  implicit val encoder: Encoder[Ping] = TypedEncoder(deriveEncoder[Ping])
  implicit val decoder: Decoder[Ping] = deriveDecoder
}

case class RemoveEndpoint(endpoint: Endpoint) extends Event

object RemoveEndpoint {
  implicit val encoder: Encoder[RemoveEndpoint] = TypedEncoder(deriveEncoder[RemoveEndpoint])
  implicit val decoder: Decoder[RemoveEndpoint] = deriveDecoder
}

case class TypedEncoder[T: ClassTag](s: Encoder[T]) extends Encoder[T] {
  override def apply(a: T): Json = s(a).deepMerge(Json.obj(("entityType", implicitly[ClassTag[T]].runtimeClass.getSimpleName.asJson)))
}