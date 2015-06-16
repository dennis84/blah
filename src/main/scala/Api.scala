package blah

import akka.actor._

class Api extends Actor {

  def receive = {
    case ArticleReq(title, body) =>
      sender ! Article(title, body)
  }
}
