package cf.jrozen.faker.kafka

case class KafkaServerInfo(host: String, port: Int = 9092) {
  def url = s"$host:$port"
}

object KafkaServerInfo {
  def localDefault: KafkaServerInfo = new KafkaServerInfo("10.1.1.21")
}