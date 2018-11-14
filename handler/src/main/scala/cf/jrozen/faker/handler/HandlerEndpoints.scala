package cf.jrozen.faker.handler

import cats.effect.{IO, Sync}
import io.circe.{Encoder, Json}
import org.http4s.Request.Keys
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, HttpRoutes, Method, Request, Response}

class HandlerEndpoints[F[_]](handlerService: HandlerService[F])(implicit S: Sync[F]) extends Http4sDsl[F] {


  def endpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@(m: Method) -> Root / requestId => {
      //      Ok()
      val value = s"$m   ${req.remoteAddr.getOrElse("unknown")} ${req.remote} $requestId ${req.toString()}"
      //      S.suspend(handlerService.emit(value))
      S.delay(Response(Accepted).withEntity(value))
      //      NoContent()
    }
  }

}

object HandlerEndpoints {
  def endpoints[F[_] : Sync](handlerService: HandlerService[F]): HttpRoutes[F] = {
    new HandlerEndpoints[F](handlerService).endpoints
  }
}


object Test extends App {

  private val rq = Request[IO]()

  import io.circe.syntax._
  //  import io.circe.._


  //  type F <: Effect[IO]

  implicit val ee = EntityEncoder.entityBodyEncoder[IO]

  implicit val encoder: Encoder[Request[IO]] = new Encoder[Request[IO]] {
    override def apply(req: Request[IO]): Json = Json.obj(
      ("method", Json.fromString(req.method.name)),
      ("uri", Json.fromString(req.uri.renderString)),
      ("httpVersion", Json.fromString(req.httpVersion.renderString)),
      ("headers", Json.arr(req.headers.map(s => Json.fromString(s.renderString)).toSeq: _*)),
      ("body", ee.toEntity(req.body).asJson),
      ("connectionInfo", Json.obj(
        ("local", Json.fromString(req.attributes.get(Keys.ConnectionInfo).map(_.local.toString).orNull)),
        ("remote", Json.fromString(req.attributes.get(Keys.ConnectionInfo).map(_.remote.toString).orNull)),
        ("secure", Json.fromBoolean(req.attributes.get(Keys.ConnectionInfo).map(_.secure).getOrElse(false)))
      )),
      ("serverSoftware", Json.fromString(req.attributes.get(Keys.ServerSoftware).map(_.product).orNull))
    )
  }

  private val json = rq.asJson.toString()
  println(json)

  //  println(decode[Endpoint](json).map(_.responseTemplate.delay.toCoarsest))
}