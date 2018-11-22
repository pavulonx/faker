package cf.jrozen.faker.api

import cats.effect._
import cats.implicits._
import cf.jrozen.faker.api.workspace.{WorkspaceEndpoints, WorkspaceService, WorkspaceValidationInterpreter}
import cf.jrozen.faker.mongo.MongoConfig
import cf.jrozen.faker.mongo.MongoConnection._
import cf.jrozen.faker.mongo.repository.WorkspaceRepository
import fs2.Stream
import org.http4s.HttpApp
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.log4s.getLogger

object ApiApp extends IOApp {

  private[this] val logger = getLogger

  override def run(args: List[String]): IO[ExitCode] = {
    serverStream[IO].compile.drain.as(ExitCode.Success)
  }

  def serverStream[F[_] : Effect : Sync : ConcurrentEffect : Timer : ContextShift]: Stream[F, ExitCode] = {
    for {
      configs <- Stream.eval(ApiConfig.load[F])
      _ <- Stream.eval(Sync[F].delay(logger.info(s"Config loaded: $configs")))

      mongoConnection <- connection[F](MongoConfig.localDefault)
      workspacesCol = mongoConnection.faker.workspaces
      workspaceRepo <- Stream.eval(Sync[F].delay(new WorkspaceRepository[F](workspacesCol)))
      workspaceValidation = WorkspaceValidationInterpreter[F](workspaceRepo)

      service = WorkspaceService[F](workspaceRepo, workspaceValidation)
      workspaceEndpoints = WorkspaceEndpoints.endpoints[F](service)

      app = Router {
        "/api" -> workspaceEndpoints
      }.orNotFound

      exitCode <- server(app)
    } yield exitCode
  }

  def server[F[_] : Sync : ConcurrentEffect : Timer](httpApp: HttpApp[F]): Stream[F, ExitCode] = {
    BlazeServerBuilder[F]
      .bindHttp(8811)
      .withHttpApp(httpApp)
      .serve
  }

}
