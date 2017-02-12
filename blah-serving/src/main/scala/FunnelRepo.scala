package blah.serving

import scala.util.{Try, Success, Failure}
import scala.concurrent._
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._

class FunnelRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  mat: Materializer
) extends SprayJsonSupport with FunnelJsonFormat {
  import system.dispatcher

  def search(q: FunnelQuery): Future[List[Funnel]] =
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "/blah/funnel/_search?size=100",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        FunnelElasticQuery(q).compactPrint)
    ) flatMap (resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      val source = 'hits / 'hits / * / '_source
      Try(json.extract[Funnel](source).toList) getOrElse Nil
    }
}
