package cf.jrozen.faker.model.domain

import java.time.Instant

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

import scala.concurrent.duration.FiniteDuration

case class Endpoint(
                     endpointId: String,

                     createdAt: Instant,

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
                             body: Option[String],
                             delay: FiniteDuration
                           )

object ResponseTemplate {
  implicit val encoder: Encoder[ResponseTemplate] = deriveEncoder
  implicit val decoder: Decoder[ResponseTemplate] = deriveDecoder
}
