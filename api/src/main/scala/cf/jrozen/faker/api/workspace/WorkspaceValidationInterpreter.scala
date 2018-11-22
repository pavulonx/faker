package cf.jrozen.faker.api.workspace

import cats._
import cats.data.EitherT
import cats.implicits._
import cf.jrozen.faker.api.{WorkspaceAlreadyExistsError, WorkspaceNotFoundError}
import cf.jrozen.faker.mongo.repository.WorkspaceRepository

class WorkspaceValidationInterpreter[F[_] : Monad](workspaceRepo: WorkspaceRepository[F]) extends WorkspaceValidationAlgebra[F] {

  def doesNotExist(workspace: WorkspaceRequest) = EitherT {
    workspaceRepo.findByName(workspace.name).map {
      case None => Right(())
      case Some(u) => Left(WorkspaceAlreadyExistsError(u))
    }
  }

  def exists(workspaceId: Option[Long]): EitherT[F, WorkspaceNotFoundError.type, Unit] = ???

  //
  //    EitherT {
  //      workspaceId.map { id =>
  //        workspaceRepo.get(id).map {
  //          case Some(_) => Right(())
  //          case _ => Left(WorkspaceNotFoundError)
  //        }
  //      }.getOrElse(
  //        Either.left[WorkspaceNotFoundError.type, Unit](WorkspaceNotFoundError).pure[F]
  //      )
  //    }
}

object WorkspaceValidationInterpreter {
  def apply[F[_] : Monad](repo: WorkspaceRepository[F]): WorkspaceValidationAlgebra[F] =
    new WorkspaceValidationInterpreter[F](repo)
}
