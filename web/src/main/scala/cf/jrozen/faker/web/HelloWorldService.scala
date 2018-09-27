package cf.jrozen.faker.web

import cats.effect.Effect
import io.circe.Json
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

class HelloWorldService[F[_] : Effect] extends Http4sDsl[F] {

  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "hello" / name =>
        Ok(Json.obj("message" -> Json.fromString(s"Hello, $name")))
      case GET -> Root / "hello2" / name =>
        NotFound(Json.obj("messasge" -> Json.fromString(s"Hello, $name")))
    }
  }
}


object Test {


  trait Negateable[F] {
    def negate(f: F): F
  }


  implicit val negateInt = new Negateable[Int] {
    override def negate(f: Int): Int = -f
  }

  implicit val negateChar = new Negateable[Char] {
    override def negate(f: Char) = (Char.MaxValue.toInt - f.toInt).toChar
  }

  def negateAll[F: Negateable](list: List[F]): List[F] = {
    list.map(implicitly[Negateable[F]].negate)
  }

  negateAll(List(1,2,3,4))
  negateAll(List(1,2,3,4))

}