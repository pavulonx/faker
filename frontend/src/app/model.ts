interface Workspace {
  name: String,
  createdAt: Date,
  endpoints: Endpoint[]
}

interface Endpoint {
  endpointId?: String,
  createdAt?: Date,
  name?: String,
  description?: String,
  response: ResponseTemplate,
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
