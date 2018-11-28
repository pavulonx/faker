package cf.jrozen.faker.api.workspace

import cats.data.EitherT
import cats.implicits._
import cats.{Functor, Monad}
import cf.jrozen.faker.api.{WorkspaceAlreadyExistsError, WorkspaceNotFoundError}
import cf.jrozen.faker.kafka.MessageProducer
import cf.jrozen.faker.model.domain.Workspace
import cf.jrozen.faker.model.messages.{Event, RemoveWorkspace}
import cf.jrozen.faker.mongo.repository.WorkspaceRepository

class WorkspaceService[F[_] : Monad : Functor](
                                                workspaceRepository: WorkspaceRepository[F],
                                                workspaceValidationAlgebra: WorkspaceValidationAlgebra[F],
                                                producer: MessageProducer[F, Event]
                                              ) {

  def addWorkspace(wsRequest: WorkspaceRequest): EitherT[F, WorkspaceAlreadyExistsError, Workspace] = {
    workspaceValidationAlgebra.doesNotExist(wsRequest).flatMap { _ =>
      val workspace = createWorkspace(wsRequest)
      EitherT.liftF(workspaceRepository.save(workspace)).map(_ => workspace)
    }
  }

  def getWorkspace(workspaceName: String): EitherT[F, WorkspaceNotFoundError, Workspace] =
    EitherT.fromOptionF(workspaceRepository.findByName(workspaceName), WorkspaceNotFoundError(workspaceName))

  def deleteWorkspace(workspaceName: String): EitherT[F, WorkspaceNotFoundError, Workspace] = for {
    ws <- getWorkspace(workspaceName)
    _ <- EitherT.liftF(workspaceRepository.deleteByName(workspaceName)) //.map(_ => ws)
    _ <- EitherT.liftF(producer.produce(workspaceName, RemoveWorkspace(workspaceName)))
  } yield ws

  def createWorkspace(wsRequest: WorkspaceRequest): Workspace = Workspace(name = wsRequest.name)

}

object WorkspaceService {
  def apply[F[_] : Monad](
                           workspacesRepository: WorkspaceRepository[F],
                           workspaceValidationAlgebra: WorkspaceValidationAlgebra[F],
                           producer: MessageProducer[F, Event]
                         ): WorkspaceService[F] =
    new WorkspaceService[F](workspacesRepository, workspaceValidationAlgebra, producer)
}