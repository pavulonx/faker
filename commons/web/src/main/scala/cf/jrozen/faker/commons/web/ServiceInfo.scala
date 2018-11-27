package cf.jrozen.faker.commons.web

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ServiceInfo(serviceName: String, version: String)

object ServiceInfo {

  @unchecked
  def apply(serviceName: String): ServiceInfo = {
    val version = getClass.getPackage.getImplementationVersion
    ServiceInfo(serviceName, version)
  }

  implicit val encoder: Encoder[ServiceInfo] = deriveEncoder
  implicit val decoder: Decoder[ServiceInfo] = deriveDecoder
}
