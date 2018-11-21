package cf.jrozen.faker.mongo

case class MongoConfig(host: String, port: Int = 27017, user: String = "root", pass: String = "pass") {
  def url = s"mongodb://$user:$pass@$host:$port"
}

object MongoConfig {
  def localDefault: MongoConfig = new MongoConfig("10.1.1.10")
}
