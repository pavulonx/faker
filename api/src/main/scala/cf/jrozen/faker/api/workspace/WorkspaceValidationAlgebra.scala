package cf.jrozen.faker.api.workspace

import cats.data.EitherT
import cf.jrozen.faker.api.{WorkspaceAlreadyExistsError, WorkspaceNotFoundError}

import scala.language.higherKinds

trait WorkspaceValidationAlgebra[F[_]] {

  def doesNotExist(workspace: WorkspaceRequest): EitherT[F, WorkspaceAlreadyExistsError, Unit]

  def exists(workspaceId: Option[Long]): EitherT[F, WorkspaceNotFoundError.type, Unit]
}
