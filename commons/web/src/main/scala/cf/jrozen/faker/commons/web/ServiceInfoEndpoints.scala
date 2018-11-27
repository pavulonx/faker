package cf.jrozen.faker.commons.web

import cats.effect.Sync
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class ServiceInfoEndpoints[F[_] : Sync] extends Http4sDsl[F] {

  def endpoints(serviceInfo: ServiceInfo): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "info" | GET -> Root / "version" =>
      Ok(serviceInfo.asJson)
  }

}

object ServiceInfoEndpoints {
  def apply[F[_] : Sync](serviceInfo: ServiceInfo): HttpRoutes[F] =
    new ServiceInfoEndpoints[F].endpoints(serviceInfo)
}




