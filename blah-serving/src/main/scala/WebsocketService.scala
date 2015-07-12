package blah.serving

import akka.actor.ActorSystem
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Sink, Source, Flow}
import Directives._

class WebsocketService(implicit system: ActorSystem) {
  import system.dispatcher

  val hub = new WebsocketHub(system)

  def route = (get & path("ws")) {
    handleWebsocketMessages(flow)
  }

  def flow = Flow[Message]
    .collect {
      case TextMessage.Strict(msg) => msg
    }
    .via(hub.flow)
    .map(msg => TextMessage.Strict(msg))
}
