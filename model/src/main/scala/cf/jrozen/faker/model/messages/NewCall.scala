package cf.jrozen.faker.model.messages

import cf.jrozen.faker.model.domain.Call
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class NewCall(call: Call) extends Event

object NewCall {
  implicit val encoder: Encoder[NewCall] = deriveEncoder
  implicit val decoder: Decoder[NewCall] = deriveDecoder
}


