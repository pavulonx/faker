package cf.jrozen.faker.api.workspace

import cats._
import cats.data.EitherT
import cats.implicits._
import cf.jrozen.faker.api.{WorkspaceAlreadyExistsError, WorkspaceNotFoundError}
import cf.jrozen.faker.model.UUID
import cf.jrozen.faker.mongo.repository.WorkspaceRepository

class WorkspaceValidationInterpreter[F[_] : Monad](workspaceRepo: WorkspaceRepository[F]) extends WorkspaceValidationAlgebra[F] {

  def doesNotExist(workspaceReq: WorkspaceRequest) = EitherT {
    find(workspaceReq) map {
      case None => Right(())
      case Some(u) => Left(WorkspaceAlreadyExistsError(u))
    }
  }

  def exists(workspaceUuid: UUID): EitherT[F, WorkspaceNotFoundError.type, Unit] = EitherT {
    workspaceRepo.findByUuid(workspaceUuid) map {
      case None => Left(WorkspaceNotFoundError)
      case Some(_) => Right(())
    }
  }

  private def find(workspaceReq: WorkspaceRequest) = {
    workspaceRepo.findByName(workspaceReq.name)
  }
}

object WorkspaceValidationInterpreter {
  def apply[F[_] : Monad](repo: WorkspaceRepository[F]): WorkspaceValidationAlgebra[F] =
    new WorkspaceValidationInterpreter[F](repo)
}
