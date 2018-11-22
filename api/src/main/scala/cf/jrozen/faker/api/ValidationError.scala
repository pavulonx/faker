package cf.jrozen.faker.api

import cf.jrozen.faker.model.Workspace


sealed trait ValidationError extends Product with Serializable
case class WorkspaceAlreadyExistsError(workspace: Workspace) extends ValidationError
case object WorkspaceNotFoundError extends ValidationError
