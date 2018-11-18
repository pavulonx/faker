package cf.jrozen.faker.handler

import cats.effect.{Sync, Timer}
import cats.implicits._
import cf.jrozen.faker.model.{ResponseTemplate => FakerResponse}
import org.http4s.headers._
import org.http4s.{EntityEncoder, MediaType, ParseFailure, Response, Status}


class ResponseTranslator[F[_]](implicit S: Sync[F], timer: Timer[F]) {

  def apply(fakerRes: FakerResponse): F[Response[F]] = {
    timer.sleep(fakerRes.delay) >> S.delay(
      (for {
        ct <- MediaType.parse(fakerRes.contentType).toOption
        sc <- (Status.fromInt(fakerRes.code): Either[ParseFailure, Status]).toOption
      } yield Response[F](sc).withEntity[String](fakerRes.body)(EntityEncoder.stringEncoder).withHeaders(`Content-Type`(ct))
        ).getOrElse[Response[F]](Response[F](Status.InternalServerError))
    )
  }
}

object ResponseTranslator {
  @inline def apply[F[_] : Timer : Sync]: ResponseTranslator[F] = new ResponseTranslator[F]()
}



