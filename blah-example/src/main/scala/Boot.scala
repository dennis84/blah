package blah.example

import scala.concurrent.duration._
import akka.actor._
import akka.util.Timeout
import akka.stream.ActorFlowMaterializer
import akka.http.scaladsl.Http
import blah.core._

object Boot extends App {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher

  val consumer = new Consumer("foo")
  consumer.read().foreach(println)
}
