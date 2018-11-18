package cf.jrozen.faker.model

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

case class Request(
                    method: String,
                    uri: String,
                    body: Option[String],
                    httpVersion: String,
                    headers: List[String],
                    connectionInfo: ConnectionInfo,

                  )

object Request {
  implicit val requestEncoder: Encoder[Request] = deriveEncoder
  implicit val requestDecoder: Decoder[Request] = deriveDecoder
}

case class ConnectionInfo(
                           remoteHost: Option[String],
                           remotePort: Option[Int],
                           secure: Option[Boolean],
                           serverHost: String,
                           serverPort: Int,
                         )

object ConnectionInfo {
  implicit val connInfoEncoder: Encoder[ConnectionInfo] = deriveEncoder
  implicit val connInfoDecoder: Decoder[ConnectionInfo] = deriveDecoder
}




