package blah.serving

import akka.actor._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._

class WebsocketRoom(system: ActorSystem) {
  private val actor = system.actorOf(Props(new Actor {
    var members = Set.empty[ActorRef]

    def receive: Receive = {
      case WebsocketRoom.Join(member) =>
        context.watch(member)
        members += member
      case WebsocketRoom.Message(event, text) =>
        members foreach (_ ! s"$event@$text")
      case Terminated(member) =>
        members -= member
      case WebsocketRoom.Leave =>
      case WebsocketRoom.Noop =>
    }
  }))

  def flow: Flow[String, String, Unit] = {
    val sink = Sink.actorRef(actor, WebsocketRoom.Leave)
    val in = Flow[String].map(x => WebsocketRoom.Noop).to(sink)
    val out = Source.actorRef(1, OverflowStrategy.fail)
      .mapMaterializedValue(actor ! WebsocketRoom.Join(_))
    Flow.fromSinkAndSource(in, out)
  }

  def send(event: String, text: String) =
    actor ! WebsocketRoom.Message(event, text)
}

object WebsocketRoom {
  case class Join(member: ActorRef)
  case object Leave
  case object Noop
  case class Message(event: String, text: String)
}
