package cf.jrozen.faker.api.workspace

import cats._
import cats.data.EitherT
import cats.implicits._
import cf.jrozen.faker.api.{WorkspaceAlreadyExistsError, WorkspaceNotFoundError}
import cf.jrozen.faker.mongo.repository.WorkspaceRepository

class WorkspaceValidationInterpreter[F[_] : Monad](workspaceRepo: WorkspaceRepository[F]) extends WorkspaceValidationAlgebra[F] {

  def doesNotExist(workspaceReq: WorkspaceRequest) = EitherT {
    workspaceRepo.findByName(workspaceReq.name) map {
      case None => Right(())
      case Some(u) => Left(WorkspaceAlreadyExistsError(u))
    }
  }

  def exists(workspaceName: String): EitherT[F, WorkspaceNotFoundError.type, Unit] = EitherT {
    workspaceRepo.findByName(workspaceName) map {
      case None => Left(WorkspaceNotFoundError)
      case Some(_) => Right(())
    }
  }

}

object WorkspaceValidationInterpreter {
  def apply[F[_] : Monad](repo: WorkspaceRepository[F]): WorkspaceValidationAlgebra[F] =
    new WorkspaceValidationInterpreter[F](repo)
}
