package blah.serving

import scala.concurrent._
import scala.util.{Try, Success, Failure}
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.elastic.ElasticClient
import blah.elastic.AggregationParser

class MostViewedRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport with ServingJsonProtocol {
  import system.dispatcher

  def find(q: MostViewedQuery): Future[List[MostViewed]] =
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "/blah/count/_search?size=0",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        MostViewedElasticQuery(q).compactPrint)
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      Try(json.extract[JsValue]('aggregations)) match {
        case Success(aggs) =>
          val items = AggregationParser.parseTo[MostViewed](aggs)
          items.take(q.limit)
        case Failure(_) => Nil
      }
    }
}
