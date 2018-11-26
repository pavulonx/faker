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

interface ResponseTemplate {
  code: number,
  contentType?: string,
  headers?: Header[],
  body?: string,
  delay?: number,
}

interface Header {
  key: string,
  value: string,
}
