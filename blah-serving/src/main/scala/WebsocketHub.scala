package blah.serving

import akka.actor._

class WebsocketHub(room: WebsocketRoom) extends Actor {
  def receive = {
    case ("count", message: String) =>
      room.send("count", message)
    case ("similarity", message: String) =>
      room.send("similarity", message)
    case ("user", message: String) =>
      room.send("user", message)
    case ("funnel", message: String) =>
      room.send("funnel", message)
    case ("most_viewed", message: String) =>
      room.send("most-viewed", message)
  }
}
