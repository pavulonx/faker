package cf.jrozen.faker.mongo

case class MongoInfo(host: String, port: Int = 27017) {
  def url = s"mongodb://$host:$port"
}

object MongoInfo {
  def localDefault: MongoInfo = new MongoInfo("localhost")
}
