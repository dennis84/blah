package blah.serving

import akka.actor._

class WebsocketHub(room: WebsocketRoom) extends Actor {
  def receive = {
    case "count" =>
      room.send("count", "[]")
    case "similarity" =>
      room.send("sim", "[]")
  }
}
