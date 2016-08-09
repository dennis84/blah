package blah.serving

import java.time.ZonedDateTime
import scala.concurrent._
import scala.concurrent.duration._
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import akka.event.Logging
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import spray.json._
import blah.core.{HttpClient, Message}
import blah.core.JsonDsl._

class JobRepoSpec
  extends FlatSpec
  with Matchers
  with MockFactory {

  class MockChronosClient extends HttpClient("localhost", 9200)

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val log = Logging.getLogger(system, this)
  import system.dispatcher

  "The JobRepo" should "list" in {
    var client = mock[MockChronosClient]
    val repo = new JobRepo(client)
    val json = List(
      ("name" -> "foo") ~
      ("lastSuccess" -> "") ~
      ("schedule" -> "R0/2015-11-26T00:00:00Z/PT10M"),
      ("name" -> "bar") ~
      ("lastSuccess" -> "2016-08-06T00:00:00Z") ~
      ("schedule" -> "R/2015-11-26T00:00:00Z/PT10M"),
      ("name" -> "baz") ~
      ("lastSuccess" -> "") ~
      ("schedule" -> "R1/2015-11-26T00:00:00Z/PT10M")
    ).compactPrint

    val csv = """|node,foo,fresh,running
                 |node,bar,success,idle
                 |node,baz,fresh,idle
                 |""".stripMargin

    (client.request _) expects(*) returning Future(
      HttpResponse(200, entity = HttpEntity(
        ContentTypes.`application/json`, json)))

    (client.request _) expects(*) returning Future(
      HttpResponse(200, entity = HttpEntity(
        ContentTypes.`application/json`, csv)))

    val resp = repo.list()

    Await.result(resp, 10.seconds) should be(List(
      Job("foo", "running", false, None),
      Job("bar", "idle", true, Some(ZonedDateTime.parse("2016-08-06T00:00:00Z"))),
      Job("baz", "idle", true, None)))
  }

  it should "run" in {
    var client = mock[MockChronosClient]
    val repo = new JobRepo(client)
    val body = (
      ("name" -> "count") ~
      ("lastSuccess" -> "") ~
      ("schedule" -> "R0/2015-11-26T00:00:00Z/PT10M")
    ).compactPrint

    (client.request _) expects(*) returning Future(
      HttpResponse(200, entity = HttpEntity(
        ContentTypes.`application/json`, body)))

    val resp = repo.run("count")

    Await.result(resp, 10.seconds) should be(Message(
      "Job has started successfully."))
  }
}
