package cf.jrozen.faker.api.users

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UserRequest(name: String)

object UserRequest {
  implicit val encoder: Encoder[UserRequest] = deriveEncoder
  implicit val decoder: Decoder[UserRequest] = deriveDecoder
}