package blah.serving

import scala.concurrent._
import scala.util.Try
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.elastic.ElasticClient

class RecommendationRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport with ServingJsonProtocol {
  import system.dispatcher

  def find(q: RecommendationQuery): Future[List[RecommendationItem]] =
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = s"/blah/recommendation/_search?size=1",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        RecommendationElasticQuery(q).compactPrint)
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      val lens = 'hits / 'hits / element(0) / '_source / 'items / *
      val items = json.extract[RecommendationItem](lens).toList
        .sortBy(- _.score)
        .take(q.limit.getOrElse(100))
      Try(items) getOrElse Nil
    }
}
