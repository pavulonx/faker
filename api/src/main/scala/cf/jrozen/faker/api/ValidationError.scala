package cf.jrozen.faker.api

import cf.jrozen.faker.model.domain.Workspace


sealed trait ValidationError extends Product with Serializable

case class WorkspaceAlreadyExistsError(workspace: Workspace) extends ValidationError
case class WorkspaceNotFoundError(workspaceName: String) extends ValidationError

case class EndpointNotFoundError(endpointId: String) extends ValidationError
