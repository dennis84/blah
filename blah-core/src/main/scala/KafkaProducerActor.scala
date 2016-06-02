package blah.core

import akka.actor.Actor
import kafka.producer.KafkaProducer
import spray.json._
import JsonProtocol._

class KafkaProducerActor(
  producer: KafkaProducer[String]
) extends Actor {
  def receive = {
    case e: Event => producer send e.toJson.compactPrint
  }
}
