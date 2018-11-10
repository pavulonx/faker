interface Endpoint {
  name?: string,
  desc?: string,
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
