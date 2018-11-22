package cf.jrozen.faker.api.workspace

import cats.Functor
import cats.effect.Effect
import cats.implicits._
import cf.jrozen.faker.api.{WorkspaceAlreadyExistsError, WorkspaceNotFoundError}
import cf.jrozen.faker.model.domain.Workspace
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpRoutes}

class WorkspaceEndpoints[F[_] : Effect : Functor] extends Http4sDsl[F] {

  implicit val workspaceRequestDecoder: EntityDecoder[F, WorkspaceRequest] = jsonOf[F, WorkspaceRequest]

  def getWorkspaceEndpoint(workspaceService: WorkspaceService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "workspace" / workspaceId =>
      workspaceService.getWorkspace(workspaceId).value >>= {
        case Right(u: Workspace) => Ok(u.asJson)
        case Left(WorkspaceNotFoundError) => NotFound()
      }
  }

  def addWorkspaceEndpoint(workspaceService: WorkspaceService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "workspace" =>
      req.as[WorkspaceRequest] >>= {
        workspaceRequest => workspaceService.addWorkspace(workspaceRequest).value
      } >>= {
        case Right(u: Workspace) => Ok(u.asJson)
        case Left(WorkspaceAlreadyExistsError(u)) => Conflict(u.asJson)
      }
  }

  def deleteWorkspaceEndpoint(workspaceService: WorkspaceService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case DELETE -> Root / "workspace" / workspaceId =>
      workspaceService.deleteWorkspace(workspaceId).value >>= {
        case Right(u: Workspace) => Ok(u.asJson)
        case Left(WorkspaceNotFoundError) => NotFound()
      }
  }

  def endpoints(workspaceService: WorkspaceService[F]): HttpRoutes[F] =
    getWorkspaceEndpoint(workspaceService) <+> addWorkspaceEndpoint(workspaceService)

}

object WorkspaceEndpoints {
  def endpoints[F[_] : Effect](workspacesService: WorkspaceService[F]): HttpRoutes[F] =
    new WorkspaceEndpoints[F]().endpoints(workspacesService)
}
