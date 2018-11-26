package cf.jrozen.faker.model.messages

import java.time.Instant

import cf.jrozen.faker.model.domain.Call
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import io.circe.{Decoder, Encoder, JsonObject, ObjectEncoder}

sealed trait Event

object Event {

  private case object Empty extends Event

  def empty: Event = Empty

  implicit val eventEncoder: Encoder[Event] = ObjectEncoder.instance {
    case Empty => JsonObject.empty
    case p@Ping(_, _) => p.asJsonObject.add("type", "Ping".asJson)
    case nc@NewCall(_) => nc.asJsonObject.add("type", "NewCall".asJson)
  }

  implicit val eventDecoder: Decoder[Event] = for {
    visitorType <- Decoder[String].prepare(_.downField("type"))
    value <- visitorType match {
      case "Ping" => Decoder[Ping]
      case "NewCall" => Decoder[NewCall]
      case other => Decoder.failedWithMessage(s"invalid type: $other")
    }
  } yield value

}

case class NewCall(call: Call) extends Event

object NewCall {
  implicit val encoder: ObjectEncoder[NewCall] = deriveEncoder
  implicit val decoder: Decoder[NewCall] = deriveDecoder
}

case class Ping(msg: String, timestamp: Instant = Instant.now) extends Event

object Ping {
  implicit val encoder: ObjectEncoder[Ping] = deriveEncoder
  implicit val decoder: Decoder[Ping] = deriveDecoder
}