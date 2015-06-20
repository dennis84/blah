package blah.core

import akka.actor._

case class GetArticle(
  val id: String)

case class CreateArticle(
  val title: String,
  val body: String)

class Api extends Actor {

  def receive = {
    case GetArticle(id) =>
      sender ! Article(id, "Hello World", "...")

    case CreateArticle(title, body) =>
      sender ! Article(Id.generate, title, body)
  }
}
