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

class CountRepo(implicit system: ActorSystem, mat: Materializer) extends SprayJsonSupport {
  import system.dispatcher

  def query(q: Query): Future[CountResult] =
    Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:9200/blah/count/_count",
      entity = HttpEntity(ContentTypes.`application/json`, q.toEs)
    )).flatMap(resp => Unmarshal(resp.entity).to[JsValue]).map { json =>
      CountResult(json.extract[Long]('count))
    }
}
