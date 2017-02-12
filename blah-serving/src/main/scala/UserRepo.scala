package blah.serving

import scala.concurrent._
import scala.util.{Try, Success, Failure}
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._

class UserRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  mat: Materializer
) extends SprayJsonSupport with UserJsonFormat {
  import system.dispatcher

  def search(q: UserQuery): Future[List[User]] =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = "/blah/user/_search?size=50&sort=date:desc",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        UserElasticQuery(q).compactPrint)
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      Try(json.extract[User]('hits / 'hits / * / '_source).toList) getOrElse Nil
    }

  def count(q: UserQuery): Future[UserCount] =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = "/blah/user/_count",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        UserElasticQuery(q).compactPrint)
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      UserCount(Try(json.extract[Long]('count)) getOrElse 0)
    }

  def grouped(q: UserQuery): Future[List[UserCount]] =
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
