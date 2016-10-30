package blah.serving

import scala.concurrent.duration._
import org.scalatest._
import akka.actor._
import akka.stream.testkit.scaladsl._
import akka.http.scaladsl.model.ws.{Message => WsMessage, TextMessage}
import akka.http.scaladsl.testkit._

class WebsocketServiceSpec
  extends FlatSpec
  with Matchers
  with ScalatestRouteTest {

  "The WebsocketHub" should "send" in {
    implicit val system = ActorSystem()

    val room = new WebsocketRoom(system)
    val hub = system.actorOf(Props(new WebsocketHub(room)))
    val service = new WebsocketService(room)
    val client = WSProbe()

    val (source, sink) = service.flow.runWith(
      TestSource.probe[WsMessage],
      TestSink.probe[WsMessage])

    source.sendNext(TextMessage("Ping"))
    sink.request(1)
    sink.expectNoMsg(100.millis)

    List(
      """count@{"foo": "bar"}""",
      """user@{"foo": "bar"}""",
      """funnel@{"foo": "bar"}""",
      """recommendation@{"foo": "bar"}"""
    ) foreach { x =>
      val parts = x.split("@", 2)
      hub ! (parts(0), parts(1))
      sink.request(1)
      sink.expectNext(TextMessage(x))
    }

    hub ! ("foo", "bar")
    sink.request(1)
    sink.expectNoMsg(100.millis)

    system.terminate()
  }
}
