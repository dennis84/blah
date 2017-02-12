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

class ReferrerRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport with ReferrerJsonFormat {
  import system.dispatcher

  def search(q: ReferrerQuery): Future[List[Referrer]] =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = s"/blah/referrer/_search?size=${q.limit.getOrElse(100)}&sort=count:desc"
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      val lens = 'hits / 'hits / * / '_source
      Try(json.extract[Referrer](lens).toList) getOrElse Nil
    }
}
