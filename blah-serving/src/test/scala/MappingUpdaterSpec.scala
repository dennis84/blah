package blah.serving

import scala.concurrent._
import scala.concurrent.duration._
import org.scalatest._
import org.scalatest.concurrent._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json._
import JsonDsl._

class MappingUpdaterSpec
  extends FlatSpec
  with Matchers
  with ScalaFutures
  with BeforeAndAfterAll {

  val indexV1: JsObject =
    ("settings" ->
      ("analysis" -> ("analyzer" ->
        ("lowercase_keyword" ->
          ("type" -> "custom") ~
          ("tokenizer" -> "keyword") ~
          ("filter" -> "lowercase"))))) ~
    ("mappings" ->
      ("foo" ->
        ("properties" ->
          ("a" -> ("type" -> "text"))
        )))

  val indexV2: JsObject =
    ("settings" ->
      ("analysis" -> ("analyzer" ->
        ("lowercase_keyword" ->
          ("type" -> "custom") ~
          ("tokenizer" -> "keyword") ~
          ("filter" -> "lowercase"))))) ~
    ("mappings" ->
      ("foo" ->
        ("properties" ->
          ("a" -> ("type" -> "text")) ~
          ("b" -> ("type" -> "text"))
        )))

  val indexV3: JsObject =
    ("settings" ->
      ("analysis" -> ("analyzer" ->
        ("lowercase_keyword" ->
          ("type" -> "custom") ~
          ("tokenizer" -> "keyword") ~
          ("filter" -> "lowercase"))))) ~
    ("mappings" ->
      ("foo" ->
        ("properties" ->
          ("a" -> ("type" -> "text")) ~
          ("b" -> ("type" -> "text")) ~
          ("c" -> ("type" -> "text"))
        )))

  val indexV4: JsObject =
    ("mappings" ->
      ("foo" ->
        ("properties" ->
          ("a" -> ("type" -> "text")))) ~
      ("bar" ->
        ("properties" ->
          ("a" -> ("type" -> "text")))))

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  "The MappingUpdater" should "create the initial index" in {
    assume(isReachable("localhost", 9200))
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val resp = Await.result(updater.update("test", indexV1), 10.seconds)
    resp should be (MappingUpdater.Created("test-1"))
  }

  it should "update to v2" in {
    assume(isReachable("localhost", 9200))
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val resp = Await.result(updater.update("test", indexV2), 10.seconds)
    resp should be (MappingUpdater.Updated("test-2"))
  }

  it should "skip, because nothing has changed" in {
    assume(isReachable("localhost", 9200))
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val resp = Await.result(updater.update("test", indexV2), 10.seconds)
    resp should be (MappingUpdater.Skipped("test-2"))
  }

  it should "update to v3" in {
    assume(isReachable("localhost", 9200))
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val resp = Await.result(updater.update("test", indexV3), 10.seconds)
    resp should be (MappingUpdater.Updated("test-3"))
  }

  it should "fail with v4" in {
    assume(isReachable("localhost", 9200))
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val fut = updater.update("test", indexV4)
    whenReady(fut.failed) { e =>
      e shouldBe a [MappingUpdater.UpdateFailed]
      val exception = e.asInstanceOf[MappingUpdater.UpdateFailed]
      exception.response.status should be (StatusCodes.BadRequest)
    }
  }

  override def beforeAll() = if(isReachable("localhost", 9200)) {
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val deleteFut = for {
      _ <- client request HttpRequest(HttpMethods.DELETE, "/test")
      _ <- client request HttpRequest(HttpMethods.DELETE, "/test-1")
      _ <- client request HttpRequest(HttpMethods.DELETE, "/test-2")
      _ <- client request HttpRequest(HttpMethods.DELETE, "/test-3")
    } yield println("Indexes deleted")
    Await.result(deleteFut, 2.seconds)
  }

  override def afterAll() = {
    val whenTerminated = system.terminate()
    Await.result(whenTerminated, 10.seconds)
  }
}
