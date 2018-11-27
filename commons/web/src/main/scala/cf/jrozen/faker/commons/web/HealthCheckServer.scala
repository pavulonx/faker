package cf.jrozen.faker.commons.web

import cats.effect.{ConcurrentEffect, ExitCode, Timer}
import fs2._
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

object HealthCheckServer {

  def apply[F[_] : ConcurrentEffect : Timer](serviceInfo: ServiceInfo, port: Int = 2137): Stream[F, ExitCode] = {
    val healthCheckApp: HttpApp[F] = Router[F](
      "/service" -> ServiceInfoEndpoints[F](serviceInfo)
    ).orNotFound

    BlazeServerBuilder[F]
      .bindHttp(port)
      .withHttpApp(healthCheckApp)
      .serve
  }

}
