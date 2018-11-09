interface Endpoint {
  name?: String,
  desc?: String,
  response: EndpointResponse,
}

interface EndpointResponse {
  code: Number,
  content?: String,
  headers?: Header[],
  body?: String,
}

interface Header {
  key: String,
  value: String,
}
