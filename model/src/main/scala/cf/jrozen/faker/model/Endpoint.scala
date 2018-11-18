package cf.jrozen.faker.model

import java.time.Instant

import io.circe._
import io.circe.generic.semiauto._

import scala.concurrent.duration.FiniteDuration

case class Endpoint(
                     uuid: String,
                     timestamp: Instant,

                     name: String,
                     description: String,

                     responseTemplate: ResponseTemplate
                   )

object Endpoint {
  implicit val encoder: Encoder[Endpoint] = deriveEncoder
  implicit val decoder: Decoder[Endpoint] = deriveDecoder
}


//add headers
case class ResponseTemplate(
                             code: Int,
                             contentType: String,
                             //                             headers: Map[String, String],
                             body: String,
                             delay: FiniteDuration
                           )


object ResponseTemplate {
  implicit val encoder: Encoder[ResponseTemplate] = deriveEncoder
  implicit val decoder: Decoder[ResponseTemplate] = deriveDecoder
}
