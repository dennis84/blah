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
import ServingJsonProtocol._

class CountRepo(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport {
  import system.dispatcher

  def count(q: Query): Future[CountResult] =
    Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:9200/blah/count/_count",
      entity = HttpEntity(ContentTypes.`application/json`, q.toEs)
    )).flatMap(resp => Unmarshal(resp.entity).to[JsValue]).map { json =>
      CountResult(json.extract[Long]('count))
    }

  def search(q: Query) =
    Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:9200/blah/count/_search",
      entity = HttpEntity(ContentTypes.`application/json`, q.toEs)
    )).flatMap(resp => Unmarshal(resp.entity).to[JsValue]).map { json =>
      val aggs = json.extract[JsValue]('aggregations)
      extract(q.groupBy map ("date" :: _.collect {
        case "user_agent.browser.family" => "browserFamily"
        case "user_agent.browser.major" => "browserMajor"
        case "user_agent.os.family" => "osFamily"
      }) getOrElse Nil, aggs)
    }

  def extract(
    groups: List[String],
    data: JsValue
  ): Seq[JsObject] = groups match {
    case Nil => Nil
    case x :: Nil => data.extract[JsValue](x / 'buckets / *) map { bucket =>
      val count = bucket.extract[JsValue]('doc_count)
      val key = bucket.extract[JsValue]('key)
      JsObject("count" -> count, x -> key)
    }
    case x :: xs => data.extract[JsValue](x / 'buckets / *) flatMap { bucket =>
      extract(xs, bucket) map {
        val key = bucket.extract[JsValue]('key)
        obj => JsObject(obj.fields ++ Map(x -> key))
      }
    }
  }
}
