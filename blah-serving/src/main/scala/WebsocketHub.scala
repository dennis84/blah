package blah.serving

import akka.actor._

class WebsocketHub(room: WebsocketRoom) extends Actor {
  def receive = {
    case ("count", message) =>
      room.send("count", message)
    case ("similarity", message) =>
      room.send("similarity", message)
    case ("user", message) =>
      room.send("user", message)
    case ("funnel", message) =>
      room.send("funnel", message)
  }
}
