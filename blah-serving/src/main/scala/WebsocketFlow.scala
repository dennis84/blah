package blah.serving

import akka.actor._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._

class WebsocketFlow(system: ActorSystem) {
  private val actor = system.actorOf(Props(new Actor {
    var members = Set.empty[ActorRef]

    def receive: Receive = {
      case WebsocketFlow.Message("count") =>
        members.foreach(_ ! WebsocketHub.Message("blah", "blubb"))
      case WebsocketFlow.Subscribe(member) =>
        members += member
      case WebsocketFlow.Close =>
    }
  }))

  def flow: Flow[String, WebsocketHub.Message, Unit] = {
    val sink = Sink.actorRef(actor, WebsocketFlow.Close)
    val in = Flow[String].map(WebsocketFlow.Message).to(sink)
    val out = Source.actorRef[WebsocketHub.Message](1, OverflowStrategy.fail)
      .mapMaterializedValue(actor ! WebsocketFlow.Subscribe(_))
    Flow.wrap(in, out)(Keep.none)
  }
}

object WebsocketFlow {
  case class Message(name: String)
  case class Subscribe(member: ActorRef)
  case object Close
}
