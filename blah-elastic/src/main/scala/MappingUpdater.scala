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

  private def aliases(actions: List[JsObject]) =
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "_aliases",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        ("actions" -> actions).compactPrint))

  def update(index: String, data: JsObject): Future[Mapping.Result] =
    getMapping(index) flatMap {
      case HttpResponse(StatusCodes.NotFound, _, _, _) => for {
        _ <- putMapping(s"$index-1", data)
        _ <- aliases(List(
          ("add" -> ("index" -> s"$index-1") ~ ("alias" -> index))))
      } yield Mapping.Created(s"$index-1")

      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        Unmarshal(entity).to[JsObject] flatMap { json =>
          val List((currentIndex, currentData: JsObject), _*) = json.fields.toList
          if(currentData == data) Future(Mapping.Skipped(currentIndex))
          else {
            val pattern = """.*-(\d+)""".r
            val n = currentIndex match {
              case pattern(x) => x.toInt + 1
            }

            for {
              _ <- putMapping(s"$index-$n", data)
              _ <- aliases(List(
                ("add" -> ("index" -> s"$index-$n") ~ ("alias" -> index)),
                ("remove" -> ("index" -> currentIndex) ~ ("alias" -> index))))
            } yield Mapping.Updated(s"$index-$n")
          }
        }
    }
}

object Mapping {
  trait Result
  case class Created(index: String) extends Result
  case class Updated(index: String) extends Result
  case class Skipped(index: String) extends Result
}
