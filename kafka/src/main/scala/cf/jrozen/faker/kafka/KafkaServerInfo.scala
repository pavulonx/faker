package cf.jrozen.faker.kafka

case class Port(int: Int) extends AnyVal

case class KafkaServerInfo(host: String, port: Port) {
  def url = s"$host:$port"
}
