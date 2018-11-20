package cf.jrozen.faker.handler

import cats.Functor
import cats.effect.Sync
import cats.implicits._
import cf.jrozen.faker.model.{ConnectionInfo => FakerConnectionInfo, Request => FakerRequest}
import org.http4s.{Charset, MediaType, Message, Request}


class RequestTranslator[F[_] : Sync](implicit F: Functor[F]) {

  private def translateBody(message: Message[F]): F[Option[String]] = {
    val charset = message.charset
    val isBinary = message.contentType.exists(_.mediaType.binary)
    val isJson = message.contentType.exists(mT =>
      mT.mediaType == MediaType.application.json || mT.mediaType == MediaType.application.`vnd.hal+json`)

    val isText = !isBinary || isJson

    val bodyStream = if (isText) {
      message.bodyAsText(charset.getOrElse(Charset.`UTF-8`))
    } else message.body
      .fold(new StringBuilder)((sb, b) => sb.append(java.lang.Integer.toHexString(b & 0xff)))
      .map(_.toString)

    bodyStream.compile.foldSemigroup
  }

  def apply(req: Request[F]): F[FakerRequest] = F.map(translateBody(req))(body =>
    FakerRequest(
      method = req.method.renderString,
      uri = req.uri.renderString,
      body = body,
      httpVersion = req.httpVersion.renderString,
      headers = req.headers.map(_.renderString).toList,
      connectionInfo = FakerConnectionInfo(req.remoteHost, req.remotePort, req.isSecure, req.serverAddr, req.serverPort),
    )
  )
}

object RequestTranslator {
  @inline def apply[F[_]](implicit F: Sync[F]): RequestTranslator[F] = new RequestTranslator[F]()
}
