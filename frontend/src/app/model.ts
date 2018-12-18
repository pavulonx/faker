interface Workspace {
  name: string,
  createdAt: Date,
  endpoints: Endpoint[]
}

interface WorkspaceRequest {
  name: string
}

interface Endpoint {
  endpointId?: string,
  createdAt?: Date,
  name?: string,
  description?: string,
  responseTemplate: ResponseTemplate,
}

interface Call {
  workspaceName: string,
  endpointId: string,
  timestamp: Date,
  request: FakerRequest
}

interface FakerRequest {
  method: string,
  uri: string,
  body: string,
  httpVersion: string,
  headers: string[],
  connectionInfo: ConnectionInfo
}

interface ConnectionInfo {
  remoteHost: string,
  remotePort: number,
  secure: boolean,
  serverHost: string,
  serverPort: number
}

interface ResponseTemplate {
  code: number,
  contentType?: string,
  // headers?: Header[],
  body?: string,
  delay?: number,
}

// interface Header {
//   key: string,
//   value: string,
// }


interface ApplicationEvent {
  entityType: string,
}

interface NewCall extends ApplicationEvent {
  call: Call,
}

interface Ping extends ApplicationEvent {
  msg: string,
  timestamp: Date
}

interface RemoveEndpoint extends ApplicationEvent {
  endpointId: string,
}

interface RemoveWorkspace extends ApplicationEvent {
  workspaceName: string,
}
