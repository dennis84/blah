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

class UserRepo(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport {
  import system.dispatcher

  def list: Future[Seq[UserResult]] =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = "/blah/users/_search?q=*:*"
    ) flatMap(resp => Unmarshal(resp.entity).to[JsValue]) map { json =>
      Try(json.extract[UserResult]('hits / 'hits / * / '_source)) getOrElse Nil
    }
}
