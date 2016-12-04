package blah.serving

import scala.concurrent._
import scala.concurrent.duration._
import org.scalatest._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model._
import blah.elastic.MappingUpdater

class CheckElasticMappingSpec extends FlatSpec with Matchers {
  "The elastic configuration" should "be valid" in {
    implicit val system = ActorSystem("test")
    implicit val ec = system.dispatcher
    implicit val mat = ActorMaterializer()
    val env = new Env

    Await.result(for {
      _ <- env.elasticClient request HttpRequest(HttpMethods.DELETE, "/blah-test")
      _ <- env.elasticClient request HttpRequest(HttpMethods.DELETE, "/blah-test-1")
      _ <- env.elasticClient request HttpRequest(HttpMethods.DELETE, "/blah-test-2")
    } yield println("Indexes deleted"), 2.seconds)

    val resp1 = Await.result(env.indexUpdater.update(
      "blah-test",
      env.elasticIndex
    ), 2.seconds)

    resp1 should be (MappingUpdater.Created("blah-test-1"))

    val resp2 = Await.result(env.indexUpdater.update(
      "blah-test",
      env.elasticIndex
    ), 2.seconds)

    resp2 should be (MappingUpdater.Skipped("blah-test-1"))
  }
}
