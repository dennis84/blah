package blah.serving

import akka.actor.ActorSystem
import akka.http.scaladsl.server._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Sink, Source, Flow}
import Directives._

class WebsocketService(
  room: WebsocketRoom
)(implicit system: ActorSystem) extends Service {
  import system.dispatcher

  def route = (get & path("ws")) {
    handleWebsocketMessages(flow)
  }

  def flow = Flow[Message]
    .collect {
      case TextMessage.Strict(msg) => msg
    }
    .via(room.flow)
    .map(msg => TextMessage.Strict(msg))
}
