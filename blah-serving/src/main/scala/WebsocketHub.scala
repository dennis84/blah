package blah.serving

import akka.actor._

class WebsocketHub(room: WebsocketRoom) extends Actor {
  def receive = {
    case ("count", message: String) =>
      room.send("count", message)
    case ("recommendation", message: String) =>
      room.send("recommendation", message)
    case ("user", message: String) =>
      room.send("user", message)
    case ("funnel", message: String) =>
      room.send("funnel", message)
    case ("events", message: String) =>
      room.send("events", message)
  }
}
