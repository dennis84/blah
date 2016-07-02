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
import blah.elastic.ElasticClient

class RecommendationRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport with ServingJsonProtocol {
  import system.dispatcher

  def find(q: RecommendationQuery): Future[RecommendationResult] =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = "/blah/recommendation/" + q.user
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      Try(json.extract[RecommendationResult]('_source)) getOrElse {
        RecommendationResult(q.user)
      }
    }
}
