package cf.jrozen.faker.web

import cats.effect.Effect
import io.circe.Json
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class HelloWorldService[F[_] : Effect] extends Http4sDsl[F] {

  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "health" =>
        Ok(Json.obj("hash" -> Json.fromString(version)))
      case GET -> Root / "hello" / name =>
        Ok(Json.obj("message" -> Json.fromString(s"Hello, $name")))
      case GET -> Root / "hello2" / name =>
        NotFound(Json.obj("messasge" -> Json.fromString(s"Hello, $name")))
    }
  }

  def version: String = "akdsljhf928h3ifad98f"

}
