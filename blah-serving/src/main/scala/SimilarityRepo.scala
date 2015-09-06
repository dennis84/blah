package blah.serving

import scala.concurrent._
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._
import ServingJsonProtocol._

class SimilarityRepo(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport {
  import system.dispatcher

  def sims(q: SimilarityQuery): Future[SimilarityResult] =
    Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:9200/blah/sims/_search",
      entity = HttpEntity(ContentTypes.`application/json`, Map(
        "query" -> Map("term" -> Map("user" -> q.user))
      ).toJson.compactPrint)
    )).flatMap(resp => Unmarshal(resp.entity).to[JsValue]).map { json =>
      val views = 'hits / 'hits / * / '_source
      json.extract[SimilarityResult](views).head
    }
}
