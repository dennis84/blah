package blah.core

import akka.actor.Actor
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import spray.json._
import JsonProtocol._

class KafkaProducerActor(
  producer: KafkaProducer[String, String],
  topic: String
) extends Actor {
  def receive = {
    case e: Event => producer.send(
      new ProducerRecord(topic, e.toJson.compactPrint))
  }
}
