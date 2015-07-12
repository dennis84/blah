package blah.serving

import akka.actor._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._

class WebsocketHub(system: ActorSystem) {
  trait Event
  case class Join(member: ActorRef) extends Event
  case object Leave extends Event
  case class Message(text: String) extends Event

  val actor = system.actorOf(Props(new Actor {
    var members = Set.empty[ActorRef]

    def receive: Receive = {
      case Join(member) =>
        context.watch(member)
        members += member
        dispatch("join!")
      case Message(text) =>
        dispatch(text)
      case text: String =>
        dispatch(text)
      case Leave =>
        dispatch("left!")
      case Terminated(member) =>
        members -= member
    }

    def dispatch(msg: String): Unit = members.foreach(_ ! msg)
  }))

  def inSink = Sink.actorRef[Event](actor, Leave)

  def outSource = Source.actorRef[String](1, OverflowStrategy.fail)

  def flow = Flow(inSink, outSource)(Keep.right) { implicit b ⇒
    (actorIn, actorOut) =>
      import FlowGraph.Implicits._
      val enveloper = b.add(Flow[String].map(Message))
      val merge = b.add(Merge[Event](2))
      enveloper ~> merge.in(0)
      b.materializedValue ~> Flow[ActorRef].map(Join) ~> merge.in(1)
      merge ~> actorIn
      (enveloper.inlet, actorOut.outlet)
    } mapMaterializedValue (_ => ())
}
