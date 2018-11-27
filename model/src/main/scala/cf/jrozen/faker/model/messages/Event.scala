package cf.jrozen.faker.model.messages

import java.time.Instant

import cf.jrozen.faker.model.messages.Event.Ping

import scala.reflect.ClassTag
// TODO: FIXME
import cf.jrozen.faker.model.domain.{Call, Endpoint}
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._

sealed trait Event

object Event {

  private case object Empty extends Event

  def empty: Event = Empty

  private implicit class JsonObjectSyntax(delegate: Json) {
    def withType(`type`: String): Json = delegate.deepMerge(Json.obj(("eventType", `type`.asJson)))
    def withType[T](implicit ct: ClassTag[T]): Json = delegate.deepMerge(Json.obj(("eventType", ct.runtimeClass.getSimpleName.asJson)))
  }

  implicit val eventEncoder: Encoder[Event] = Encoder.instance {
    case Empty => JsonObject.empty.asJson
    case p@Ping(_, _) => p.asJson.withType("Ping")
    case n@NewCall(_) => n.asJson.withType("NewCall")
    case r@RemoveEndpoint(_) => r.asJson.withType("RemoveEndpoint")
  }




  implicit val eventDecoder: Decoder[Event] = for {
    visitorType <- Decoder[String].prepare(_.downField("eventType"))
    value <- visitorType match {
      case "Ping" => Decoder[Ping]
      case "NewCall" => Decoder[NewCall]
      case "RemoveEndpoint" => Decoder[RemoveEndpoint]
      case other => Decoder.failedWithMessage(s"invalid type: $other")
    }
  } yield value



  case class NewCall(call: Call) extends Event

  object NewCall {
    implicit val encoder: Encoder[NewCall] = Encoder.instance { nc =>Json.obj(
      ("call", nc.call.asJson)
    ).withType[NewCall]

    }
    implicit val decoder: Decoder[NewCall] = deriveDecoder
  }

  case class Ping(msg: String, timestamp: Instant = Instant.now) extends Event

  object Ping {
    implicit val encoder: Encoder[Ping] = Encoder.instance { ping =>Json.obj(
      ("msg", ping.msg.asJson),
      ("timestamp", ping.timestamp.asJson)
    ).withType[Ping]

    }
    implicit val decoder: Decoder[Ping] = deriveDecoder
  }

  case class RemoveEndpoint(endpoint: Endpoint) extends Event

  object RemoveEndpoint {
    implicit val encoder: Encoder[RemoveEndpoint] = deriveEncoder
    implicit val decoder: Decoder[RemoveEndpoint] = deriveDecoder
  }

}

object Test extends App {
  private val str: String = Ping("""asdasd""").asJson.toString()
  println(str)
  println(io.circe.parser.decode[Event](str))
}
