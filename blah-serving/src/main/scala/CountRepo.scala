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
import blah.elastic.ElasticClient
import blah.elastic.AggregationParser
import ServingJsonProtocol._

class CountRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport {
  import system.dispatcher

  def count(q: CountQuery): Future[CountResult] = client request HttpRequest(
    method = HttpMethods.POST,
    uri = "/blah/count/_search?size=0",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      CountElasticQuery.filtered(q).compactPrint)
  ) flatMap (resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
    CountResult(json.extract[Long]('aggregations / 'count / 'value))
  }

  def search(q: CountQuery): Future[Seq[JsObject]] = client request HttpRequest(
    method = HttpMethods.POST,
    uri = "/blah/count/_search?size=0",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      CountElasticQuery.grouped(q).compactPrint)
  ) flatMap (resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
    val aggs = json.extract[JsValue]('aggregations)
    AggregationParser.parse(aggs)
  }
}
