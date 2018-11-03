package cf.jrozen.faker.mongo

case class MongoConfig(host: String, port: Int = 27017) {
  def url = s"mongodb://$host:$port"
}

object MongoConfig {
  def localDefault: MongoConfig = new MongoConfig("localhost")
}
