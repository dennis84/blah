package blah.test

import java.util.UUID
import org.scalatest._
import org.scalatest.time._
import org.scalatest.concurrent._
import org.scalatest.selenium._
import akka.actor._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import blah.http.HttpClient

class UserSpec extends FlatSpec with Matchers with Chrome with Eventually {

  val host = "http://ui.blah.local"

  "User features" should "work" in {
    implicit val system = ActorSystem("test")
    implicit val executor = system.dispatcher
    implicit val materializer = ActorMaterializer()
    lazy val api = new HttpClient("api.blah.local", 80)

    val user = UUID.randomUUID.toString
    api request HttpRequest(
      method = HttpMethods.POST,
      uri = "/events/test",
      entity = HttpEntity(
        ContentTypes.`application/json`, s"""{
          "item": "landingpage",
          "title": "Viewed landing page",
          "user": "$user",
          "referrer": "google.com"
        }"""))

    go to (host + "/#/jobs")
    click on button("user")
  
    eventually(timeout(Span(60, Seconds)), interval(Span(5, Seconds))) {
      find(contains("a few seconds ago")).isDefined should be (true)
    }

    go to (host + "/#/people")

    val userElem = find(contains(user))
    userElem.isDefined should be (true)

    quit()
  }

  def button(name: String) =
    xpath(s"//p[contains(@class, 'card-header-title') and text() = '$name']/following::a[1]")

  def contains(value: String) =
    xpath(s"//*[contains(text(),'$value')]")
}
