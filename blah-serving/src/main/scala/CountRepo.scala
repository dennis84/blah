package blah.serving

import scala.util.{Try, Success, Failure}
import scala.concurrent._
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.elastic.ElasticClient
import blah.elastic.AggregationParser

class CountRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport with ServingJsonProtocol {
  import system.dispatcher

  def count(q: Query): Future[Count] = client request HttpRequest(
    method = HttpMethods.POST,
    uri = "/blah/count/_search?size=0",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      CountElasticQuery(q).compactPrint)
  ) flatMap (resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
    val sum = 'aggregations / 'count / 'value
    Count(Try(json.extract[Long](sum)) getOrElse 0)
  }

  def search(q: Query): Future[List[Count]] = client request HttpRequest(
    method = HttpMethods.POST,
    uri = "/blah/count/_search?size=0",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      CountElasticQuery(q).compactPrint)
  ) flatMap (resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
    Try(json.extract[JsValue]('aggregations)) match {
      case Success(aggs) => AggregationParser.parseTo[Count](aggs)
      case Failure(_) => Nil
    }
  }
}
