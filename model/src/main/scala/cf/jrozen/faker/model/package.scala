package cf.jrozen.faker

import java.util.UUID

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

import scala.concurrent.duration.FiniteDuration

package object model {

  implicit val finiteDurationEncoder: Encoder[FiniteDuration] = new Encoder[FiniteDuration] {
    override def apply(fd: FiniteDuration): Json = Json.fromLong(fd toMillis)
  }

  implicit val finiteDurationDecoder: Decoder[FiniteDuration] = new Decoder[FiniteDuration] {

    import scala.concurrent.duration._

    override def apply(c: HCursor): Result[FiniteDuration] = Decoder.decodeLong(c).map(_ milliseconds)
  }

  def rndUuid(): String = UUID.randomUUID().toString
}
