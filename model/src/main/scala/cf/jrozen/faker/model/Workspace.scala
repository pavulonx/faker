package cf.jrozen.faker.model

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}


case class Workspace(
                      name: String,
                      createdAt: Instant = Instant.now,
                      endpoints: List[Endpoint] = List()
                    )

object Workspace {
  implicit val encoder: Encoder[Workspace] = deriveEncoder
  implicit val decoder: Decoder[Workspace] = deriveDecoder
}
