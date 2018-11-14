package cf.jrozen.faker.handler

import cats.effect.{Effect, IO, Sync}
import io.circe.{Encoder, Json}
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Method, Request, Response}

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


  type F <: Effect[F]

  implicit val encoder: Encoder[Request[F]] = new Encoder[Request[F]] {
    override def apply(req: Request[F]): Json = Json.obj (
      ("method", Json.fromString(req.method.name)),
      ("uri", Json.fromString(req.uri)),
      ("httpVersion", Json.fromString(req.httpVersion)),
      ("headers", Json.fromString(req.headers)),
      ("body", Json.fromString(req.body)),
      ("attributes", Json.fromString(req.attributes)),
      (),
      ()
    )
  }

  private val json = rq.asJson.toString()
  println(json)

  //  println(decode[Endpoint](json).map(_.responseTemplate.delay.toCoarsest))
}