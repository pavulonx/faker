package cf.jrozen.faker.model

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}


case class Call(
                 callUuid: String,
                 endpointUuid: String,
                 timestamp: Instant,

                 request: Request
               )


object Call {
  implicit val encoder: Encoder[Call] = deriveEncoder
  implicit val decoder: Decoder[Call] = deriveDecoder
}
