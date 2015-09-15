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

class CountRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport {
  import system.dispatcher

  def count(q: CountQuery): Future[CountResult] = client request HttpRequest(
    method = HttpMethods.POST,
    uri = "/blah/count/_count",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      CountQueryToEs(q).map(_.compactPrint).getOrElse(""))
  ) flatMap (resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
    CountResult(json.extract[Long]('count))
  }

  def search(q: CountQuery): Future[Seq[JsObject]] = client request HttpRequest(
    method = HttpMethods.POST,
    uri = "/blah/count/_search",
    entity = HttpEntity(
      ContentTypes.`application/json`,
      CountQueryToEs(q).map(_.compactPrint).getOrElse(""))
  ) flatMap (resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
    val aggs = json.extract[JsValue]('aggregations)
    CountResponseParser.parse(q.groupBy map ("date" :: _.collect {
      case "user_agent.browser.family" => "browserFamily"
      case "user_agent.browser.major" => "browserMajor"
      case "user_agent.os.family" => "osFamily"
    }) getOrElse Nil, aggs)
  }
}
