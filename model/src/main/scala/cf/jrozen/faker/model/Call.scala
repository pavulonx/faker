package cf.jrozen.faker.model

import java.time.Instant

import cf.jrozen.faker.model.Call.Remote
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}


case class Call(
                 uuid: String,
                 timestamp: Instant,

                 remote: Remote,

               )


object Call {

  type Remote = String


  implicit val encoder: Encoder[Call] = deriveEncoder
  implicit val decoder: Decoder[Call] = deriveDecoder
}
