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

class SimilarityRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport {
  import system.dispatcher

  def sims(q: SimilarityQuery): Future[SimilarityResult] =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = "/blah/sims/" + q.user
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      Try(json.extract[SimilarityResult]('_source)) getOrElse SimilarityResult(q.user)
    }
}
