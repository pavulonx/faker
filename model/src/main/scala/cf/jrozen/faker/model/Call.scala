package cf.jrozen.faker.model

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Call(
                 uuid: String,
                 timestamp: Instant,

               )

object Call {

  object Endpoint {
    implicit val encoder: Encoder[Call] = deriveEncoder
    implicit val decoder: Decoder[Call] = deriveDecoder
  }

}