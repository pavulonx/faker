package cf.jrozen.faker.handler

import cats.Functor
import cats.effect.Sync
import cats.implicits._
import io.circe.syntax._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Method, Response}

class HandlerEndpoints[F[_] : Functor](handlerService: HandlerService[F])(implicit S: Sync[F]) extends Http4sDsl[F] {

  def endpoints: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@(m: Method) -> Root / requestId =>
      RequestTranslator[F].apply(req).map(result =>
        Response(Accepted).withEntity(result.asJson.toString)
      )
  }
}

object HandlerEndpoints {
  def endpoints[F[_] : Sync](handlerService: HandlerService[F]): HttpRoutes[F] = {
    new HandlerEndpoints[F](handlerService).endpoints
  }
}

//object Test extends App {
//  private val rq = Request[IO]().withEntity("asdasdasdsa")
//
//  import io.circe.syntax._
//
//  RequestTranslator[IO].apply(rq).flatMap(s => IO {
//    println(s.asJson)
//  }).unsafeRunSync()
//
//}