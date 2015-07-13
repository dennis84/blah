package blah.core

import akka.actor._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._

class WebsocketHub(system: ActorSystem) {
  trait WebsocketEvent
  case class Join(member: ActorRef) extends WebsocketEvent
  case object Leave extends WebsocketEvent
  case object Noop extends WebsocketEvent
  case class Message(event: String, text: String) extends WebsocketEvent

  private val actor = system.actorOf(Props(new Actor {
    var members = Set.empty[ActorRef]

    def receive: Receive = {
      case Join(member) =>
        context.watch(member)
        members += member
      case Message(event, text) =>
        dispatch(s"$event@$text")
      case Terminated(member) =>
        members -= member
      case Leave =>
      case Noop =>
    }

    def dispatch(msg: String): Unit = members.foreach(_ ! msg)
  }))

  private def inSink = Sink.actorRef[WebsocketEvent](actor, Leave)

  private def outSource = Source.actorRef[String](1, OverflowStrategy.fail)

  def flow = Flow(inSink, outSource)(Keep.right) { implicit b ⇒
    (actorIn, actorOut) =>
      import FlowGraph.Implicits._
      val enveloper = b.add(Flow[String].map(x => Noop))
      val merge = b.add(Merge[WebsocketEvent](2))
      enveloper ~> merge.in(0)
      b.materializedValue ~> Flow[ActorRef].map(Join) ~> merge.in(1)
      merge ~> actorIn
      (enveloper.inlet, actorOut.outlet)
    } mapMaterializedValue (_ => ())

  def send(event: String, text: String) {
    actor ! Message(event, text)
  }
}
