package cf.jrozen.faker.mongo

import java.time.Instant

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import cf.jrozen.faker.model.User
import cf.jrozen.faker.mongo.MongoConnection.connection
import cf.jrozen.faker.mongo.repository.UserRepository
import fs2.Stream

object test extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      conn <- connection[IO](MongoConfig.localDefault)
      colection = conn.getDatabase("test_db").getCollection("users_test")
      ur <- Stream.eval(IO(new UserRepository[IO](colection)))
      _ <- Stream.eval(ur.save(User("uuid1", Instant.now, "name", List())))
      res <- ur.getByName("name")
      _ <- Stream.eval(IO(println(res)))
    } yield ()
    stream.compile.drain.as(ExitCode.Success)
  }

}
