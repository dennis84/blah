package blah.serving

import akka.actor._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._

class WebsocketFlow(system: ActorSystem) {
  private val actor = system.actorOf(Props(new Actor {
    def receive: Receive = {
      case WebsocketFlow.Message(name) =>
        println(name)
      case WebsocketFlow.Subscribe =>
        println("Subscribe")
      case WebsocketFlow.Close =>
        println("Close")
    }
  }))

  def flow: Flow[String, String, Unit] = {
    val sink = Sink.actorRef(actor, WebsocketFlow.Close)
    val in = Flow[String].map(WebsocketFlow.Message).to(sink)
    val out = Source.actorRef[String](1, OverflowStrategy.fail)
      .mapMaterializedValue(_ => actor ! WebsocketFlow.Subscribe)
    Flow.wrap(in, out)(Keep.none)
  }
}

object WebsocketFlow {
  case class Message(name: String)
  case object Subscribe
  case object Close
}
