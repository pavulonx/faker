package cf.jrozen.faker.api.workspace

import cats.data.EitherT
import cats.{Functor, Monad}
import cf.jrozen.faker.api.{WorkspaceAlreadyExistsError, WorkspaceNotFoundError}
import cf.jrozen.faker.model.Workspace
import cf.jrozen.faker.mongo.repository.WorkspaceRepository

class WorkspaceService[F[_] : Monad : Functor](workspaceRepository: WorkspaceRepository[F], workspaceValidationAlgebra: WorkspaceValidationAlgebra[F]) {

  def addWorkspace(wsRequest: WorkspaceRequest): EitherT[F, WorkspaceAlreadyExistsError, Workspace] = {
    workspaceValidationAlgebra.doesNotExist(wsRequest).flatMap { _ =>
      val workspace = createWorkspace(wsRequest)
      EitherT.liftF(workspaceRepository.save(workspace)).map(_ => workspace)
    }
  }

  def getWorkspace(workspaceName: String): EitherT[F, WorkspaceNotFoundError.type, Workspace] =
    EitherT.fromOptionF(workspaceRepository.findByName(workspaceName), WorkspaceNotFoundError)

  def deleteWorkspace(workspaceName: String): EitherT[F, WorkspaceNotFoundError.type, Workspace] =
    getWorkspace(workspaceName).flatMap { ws =>
      EitherT.liftF(workspaceRepository.deleteByName(workspaceName)).map(_ => ws)
    }

  def createWorkspace(wsRequest: WorkspaceRequest): Workspace = Workspace(name = wsRequest.name)

}

object WorkspaceService {
  def apply[F[_] : Monad](workspacesRepository: WorkspaceRepository[F], workspaceValidationAlgebra: WorkspaceValidationAlgebra[F]): WorkspaceService[F] =
    new WorkspaceService[F](workspacesRepository, workspaceValidationAlgebra)
}