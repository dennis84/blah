package blah.serving

import scala.concurrent._
import scala.util.Try
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.elastic.ElasticClient
import ServingJsonProtocol._

class UserRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport {
  import system.dispatcher

  def count(q: UserQuery): Future[UserCount] =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = "/blah/users/_count"
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      Try(UserCount(json.extract[Long]('count))) getOrElse UserCount(0)
    }

  def search(q: UserQuery): Future[Seq[JsObject]] =
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "/blah/users/_search?size=0",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        UserElasticQuery.grouped(q).compactPrint)
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      val aggs = json.extract[JsValue]('aggregations)
      UserResponseParser.parse(q.groupBy.getOrElse(Nil), aggs)
    }
}
