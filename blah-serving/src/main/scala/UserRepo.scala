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

class UserRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport with ServingJsonProtocol {
  import system.dispatcher

  def count(q: Query): Future[UserCount] =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = "/blah/user/_count"
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      UserCount(Try(json.extract[Long]('count)) getOrElse 0)
    }

  def search(q: Query): Future[List[UserCount]] =
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "/blah/user/_search?size=0",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        UserElasticQuery(q).compactPrint)
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      Try(json.extract[JsValue]('aggregations)) match {
        case Success(aggs) => AggregationParser.parseTo[UserCount](aggs)
        case Failure(_) => Nil
      }
    }
}
