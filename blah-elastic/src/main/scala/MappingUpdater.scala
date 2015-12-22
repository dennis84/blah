package blah.elastic

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import blah.core.JsonDsl._

class MappingUpdater(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport with DefaultJsonProtocol {
  import system.dispatcher

  private def getMapping(index: String) =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = s"/$index/_mapping")

  private def putMapping(index: String, mapping: JsObject) =
    client request HttpRequest(
      method = HttpMethods.PUT,
      uri = s"/$index",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        mapping.compactPrint))

  private def addAlias(index: String, alias: String) = {
    val json: JsObject = ("actions" -> List(
      ("add" -> ("index" -> index) ~ ("alias" -> alias))))
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "_aliases",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        json.compactPrint))
  }

  private def switchAlias(newIndex: String, oldIndex: String, alias: String) = {
    val json: JsObject = ("actions" -> List(
      ("remove" -> ("index" -> oldIndex) ~ ("alias" -> alias)) ~
      ("add" -> ("index" -> newIndex) ~ ("alias" -> alias))))
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "_aliases",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        json.compactPrint))
  }

  def update(index: String, data: JsObject): Future[MappingUpdater.Response] =
    getMapping(index) flatMap {
      case HttpResponse(StatusCodes.NotFound, _, _, _) => for {
        mapping <- putMapping(s"$index-1", data)
        alias   <- addAlias(s"$index-1", index)
      } yield MappingUpdater.Response("created", s"$index-1")

      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        Unmarshal(entity).to[JsObject] flatMap { json =>
          val List((currentIndex, currentData: JsObject), _*) = json.fields.toList
          if(currentData == data) Future(MappingUpdater.Response("skipped", currentIndex))
          else {
            val pattern = """.*-(\d+)""".r
            val n = currentIndex match {
              case pattern(x) => x.toInt + 1
            }

            for {
              mapping <- putMapping(s"$index-$n", data)
              alias   <- switchAlias(s"$index-$n", s"$currentIndex", index)
            } yield MappingUpdater.Response("updated", s"$index-$n")
          }
        }
    }
}

object MappingUpdater {
  case class Response(status: String, index: String)
}
