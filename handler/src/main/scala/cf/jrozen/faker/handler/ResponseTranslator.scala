package cf.jrozen.faker.handler

import cats.effect.{Sync, Timer}
import cats.implicits._
import cf.jrozen.faker.model.domain.ResponseTemplate
import org.http4s.headers._
import org.http4s.{EntityEncoder, MediaType, ParseFailure, Response, Status}


class ResponseTranslator[F[_]](implicit S: Sync[F], timer: Timer[F]) {

  def apply(fakerRes: ResponseTemplate): F[Response[F]] = {
    timer.sleep(fakerRes.delay) >> S.delay(
      (for {
        sc <- Status.fromInt(fakerRes.code): Either[ParseFailure, Status]
      } yield Response[F](sc).withEntity[String](fakerRes.body.getOrElse(""))(EntityEncoder.stringEncoder).withHeaders(`Content-Type`(MediaType.parse(fakerRes.contentType).getOrElse(MediaType.text.plain)))
        ).getOrElse[Response[F]](Response[F](Status.InternalServerError))
    )
  }
}

object ResponseTranslator {
  @inline def apply[F[_] : Timer : Sync]: ResponseTranslator[F] = new ResponseTranslator[F]()
}



