package blah.serving

import scala.concurrent._
import scala.util.Try
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.elastic.ElasticClient

class SimilarityRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  mat: Materializer
) extends SprayJsonSupport with SimilarityJsonFormat {
  import system.dispatcher

  def find(q: SimilarityQuery): Future[List[SimilarityItem]] =
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "/blah/similarity/_search?size=50",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        SimilarityElasticQuery(q).compactPrint)
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      val lens = 'aggregations / 'sims / 'items / 'hits / 'hits / * / '_source
      Try {
        json.extract[SimilarityItem](lens)
          .groupBy(_.item)
          .map(_._2.sortBy(_.score).last)
          .toList
      } getOrElse Nil
    }
}
