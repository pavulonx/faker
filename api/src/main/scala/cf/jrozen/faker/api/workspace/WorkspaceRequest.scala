package cf.jrozen.faker.api.workspace

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class WorkspaceRequest(name: String)

object WorkspaceRequest {
  implicit val encoder: Encoder[WorkspaceRequest] = deriveEncoder
  implicit val decoder: Decoder[WorkspaceRequest] = deriveDecoder
}