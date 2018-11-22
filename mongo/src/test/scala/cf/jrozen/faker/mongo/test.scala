package cf.jrozen.faker.mongo

import java.time.Instant

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import cf.jrozen.faker.model.domain.Workspace
import cf.jrozen.faker.mongo.MongoConnection.connection
import cf.jrozen.faker.mongo.repository.WorkspaceRepository
import fs2.Stream

object test extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      conn <- connection[IO](MongoConfig.localDefault)
      colection = conn.getDatabase("test_db").getCollection("users_test")
      ur <- Stream.eval(IO(new WorkspaceRepository[IO](colection)))
      _ <- Stream.eval(ur.save(Workspace("name", Instant.now, List())))
      res <- Stream.eval(ur.findByName("name"))
      _ <- Stream.eval(IO(println(res)))
    } yield ()
    stream.compile.drain.as(ExitCode.Success)
  }

}
