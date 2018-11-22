package cf.jrozen.faker.model

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}


case class User(
            userUuid: String = rndUuid(),
            timestamp: Instant = Instant.now,

            name: String,

            endpoints: List[Endpoint] = List()
          )

object User {
  implicit val encoder: Encoder[User] = deriveEncoder
  implicit val decoder: Decoder[User] = deriveDecoder
}
