package cf.jrozen.faker.api.dto

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ClientRequest(name: String)

object ClientRequest {
  implicit val encoder: Encoder[ClientRequest] = deriveEncoder
  implicit val decoder: Decoder[ClientRequest] = deriveDecoder
}