package cf.jrozen.faker.model

import java.time.Instant

import io.circe._
import io.circe.generic.semiauto._

case class Ping(msg: String, timestamp: Long = Instant.now.toEpochMilli)


object Ping {
  implicit val encoder: Encoder[Ping] = deriveEncoder[Ping]
  implicit val decoder: Decoder[Ping] = deriveDecoder[Ping]
}
