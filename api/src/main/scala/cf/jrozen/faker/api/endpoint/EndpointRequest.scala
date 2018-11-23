package cf.jrozen.faker.api.endpoint

import cf.jrozen.faker.model.domain.ResponseTemplate
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class EndpointRequest(
                            name: String,
                            description: String,

                            responseTemplate: ResponseTemplate
                          )

object EndpointRequest {
  implicit val encoder: Encoder[EndpointRequest] = deriveEncoder
  implicit val decoder: Decoder[EndpointRequest] = deriveDecoder
}
