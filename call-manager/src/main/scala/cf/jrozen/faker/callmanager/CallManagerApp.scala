package cf.jrozen.faker.callmanager

import cats.data.NonEmptyList
import cats.effect._
import cats.implicits._
import cf.jrozen.faker.kafka.KafkaConfiguration
import cf.jrozen.faker.model.messages.Event
import cf.jrozen.faker.mongo.MongoConfig
import cf.jrozen.faker.mongo.MongoConnection.{connection, _}
import cf.jrozen.faker.mongo.repository.CallRepository
import fs2.Stream
import fs2.kafka.{consumerExecutionContextStream, consumerStream}
import org.apache.kafka.clients.consumer.ConsumerRecord

object CallManagerApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      conf <- Stream.eval(CallManagerConfig.load[IO])

      mongoConnection <- connection[IO](MongoConfig.localDefault)
      callEventsCol = mongoConnection.faker.callEvents
      callRepo <- Stream.eval(Sync[IO].delay(CallRepository.mutable[IO](callEventsCol)))
      service = CallManagerService[IO](callRepo)

      app <- kafkaStream[IO](conf).evalMap(_.value()).evalMap[IO, Unit](service.process)
    } yield app

  }.compile.drain.as(ExitCode.Success)


  def kafkaStream[F[_] : ConcurrentEffect : ContextShift : Timer](callManagerConfig: CallManagerConfig
                                                                 ): Stream[F, ConsumerRecord[String, F[Event]]] = for {
    executionContext <- consumerExecutionContextStream[F]
    consumerSettings = KafkaConfiguration.consumerSettings[F, Event]("call_manager", callManagerConfig.kafka)
    consumer <- consumerStream[F].using(consumerSettings(executionContext))
    _ <- Stream.eval(consumer.subscribe(NonEmptyList.one(callManagerConfig.notificationsTopic)))
    message <- consumer.stream
  } yield message.record

}
