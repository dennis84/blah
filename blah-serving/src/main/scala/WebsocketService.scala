package blah.serving

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.Flow

class WebsocketService(
  room: WebsocketRoom
)(implicit system: ActorSystem) extends Service {
  import system.dispatcher

  def route = (get & path("ws")) {
    handleWebSocketMessages(flow)
  }

  def flow = Flow[Message]
    .collect {
      case TextMessage.Strict(msg) => msg
    }
    .via(room.flow)
    .map(msg => TextMessage.Strict(msg))
}
