package blah.elastic

import scala.concurrent._
import scala.concurrent.duration._
import org.scalatest._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import spray.json._
import blah.core.JsonDsl._

class MappingUpdaterSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  val mappingV1: JsObject =
    ("mappings" ->
      ("foo" ->
        ("properties" ->
          ("a" -> ("type" -> "string"))
        )))

  val mappingV2: JsObject =
    ("mappings" ->
      ("foo" ->
        ("properties" ->
          ("a" -> ("type" -> "string")) ~
          ("b" -> ("type" -> "string"))
        )))

  val mappingV3: JsObject =
    ("mappings" ->
      ("foo" ->
        ("properties" ->
          ("a" -> ("type" -> "string")) ~
          ("b" -> ("type" -> "string")) ~
          ("c" -> ("type" -> "string"))
        )))

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  "The MappingUpdater" should "create the initial mapping" in {
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val resp = Await.result(updater.update("test", mappingV1), 10.seconds)
    resp should be (Mapping.Created("test-1"))
  }

  it should "update to v2" in {
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val resp = Await.result(updater.update("test", mappingV2), 10.seconds)
    resp should be (Mapping.Updated("test-2"))
  }

  it should "skip, because nothing has changed" in {
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val resp = Await.result(updater.update("test", mappingV2), 10.seconds)
    resp should be (Mapping.Skipped("test-2"))
  }

  it should "update to v3" in {
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val updater = new MappingUpdater(client)
    val resp = Await.result(updater.update("test", mappingV3), 10.seconds)
    resp should be (Mapping.Updated("test-3"))
  }

  override def beforeAll() = {
    val client = new ElasticClient(ElasticUri("localhost:9200"))
    val deleteFut = for {
      _ <- client request HttpRequest(HttpMethods.DELETE, "/test")
      _ <- client request HttpRequest(HttpMethods.DELETE, "/test-v1")
      _ <- client request HttpRequest(HttpMethods.DELETE, "/test-v2")
      _ <- client request HttpRequest(HttpMethods.DELETE, "/test-v3")
    } yield println("Indexes deleted")
    Await.result(deleteFut, 2.seconds)
  }

  override def afterAll() = {
    val whenTerminated = system.terminate()
    Await.result(whenTerminated, 10.seconds)
  }
}
