package cf.jrozen.faker.model

import java.time.Instant

import io.circe._
import io.circe.generic.JsonCodec
import io.circe.generic.semiauto._

import scala.concurrent.duration.FiniteDuration

case class Endpoint(
                     uuid: String,
                     timestamp: Instant,

                     name: String,
                     description: String,

                     responseTemplate: ResponseTemplate
                   )

object Endpoint {
  implicit val encoder: Encoder[Endpoint] = deriveEncoder
  implicit val decoder: Decoder[Endpoint] = deriveDecoder
}


//add headers
case class ResponseTemplate(
                             code: Int,
                             contentType: String,
//                             headers: Map[String, String],
                             body: String,
                             delay: FiniteDuration
                           )


object ResponseTemplate {
  implicit val encoder: Encoder[ResponseTemplate] = deriveEncoder
  implicit val decoder: Decoder[ResponseTemplate] = deriveDecoder
}

object Test extends App {

  import io.circe.syntax._

  private val endp = Endpoint("qwer", Instant.now(), "dupa", "dome dupa endpoint", ResponseTemplate(1, "text", "chuj", "{x : {}}", FiniteDuration(1, "second")))

  private val json = endp.asJson.toString()
  println(json)

  //  println(decode[Endpoint](json).map(_.responseTemplate.delay.toCoarsest))
}