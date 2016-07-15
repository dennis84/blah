package blah.serving

import scala.concurrent._
import scala.concurrent.duration._
import org.scalatest._
import org.scalamock.scalatest.MockFactory
import akka.actor._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import spray.json._
import blah.core.JsonDsl._
import blah.elastic._

class RecommendationRepoSpec
  extends FlatSpec
  with Matchers
  with MockFactory {

  "The RecommendationRepo" should "find by user" in {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import system.dispatcher

    class MockElasticClient extends ElasticClient(
      ElasticUri("localhost:9200"))

    var client = mock[MockElasticClient]
    val repo = new RecommendationRepo(client)
    val body = ("hits" -> ("hits" -> List(
      ("_source" ->
        ("user" -> "dennis") ~
        ("collection" -> "products") ~
        ("items" -> List(
          ("item" -> "a") ~ ("score" -> 1.0),
          ("item" -> "b") ~ ("score" -> 0.7),
          ("item" -> "c") ~ ("score" -> 0.5)))
      )))).compactPrint

    (client.request _).expects(*) returning {
      Future(HttpResponse(200, entity = HttpEntity(
        ContentTypes.`application/json`, body)))
    }

    val resp = repo.find(RecommendationQuery("dennis", Some("products")))

    Await.result(resp, 10.seconds) should be (List(
      RecommendationItem("a", 1.0),
      RecommendationItem("b", 0.7),
      RecommendationItem("c", 0.5)))
  }
}
