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
  import MappingUpdater._

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

  def update(index: String, data: JsObject): Future[Result] =
    getMapping(index) flatMap {
      case HttpResponse(StatusCodes.NotFound, _, _, _) => for {
        r <- putMapping(s"$index-1", data)
        _ = if(r.status.isFailure) throw UpdateFailed(r)
        r <- aliases(List(
          ("add" -> ("index" -> s"$index-1") ~ ("alias" -> index))))
        _ = if(r.status.isFailure) throw UpdateFailed(r)
      } yield Created(s"$index-1")

      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        Unmarshal(entity).to[JsObject] flatMap { json =>
          val pattern = """.*-(\d+)""".r
          json.fields.toList match {
            case (currentIndex, currentData) :: Nil if(currentData == data) =>
              Future(Skipped(currentIndex))
            case (currentIndex@pattern(x), currentData) :: Nil =>
              val nextIndex = x.toInt + 1
              for {
                r <- putMapping(s"$index-$nextIndex", data)
                _ = if(r.status.isFailure) throw UpdateFailed(r)
                r <- aliases(List(
                  ("add" -> ("index" -> s"$index-$nextIndex") ~ ("alias" -> index)),
                  ("remove" -> ("index" -> currentIndex) ~ ("alias" -> index))))
                _ = if(r.status.isFailure) throw UpdateFailed(r)
              } yield Updated(s"$index-$nextIndex")
          }
        }
    }
}

object MappingUpdater {
  sealed trait Result
  case class Created(index: String) extends Result
  case class Updated(index: String) extends Result
  case class Skipped(index: String) extends Result
  case class UpdateFailed(val response: HttpResponse)
    extends Exception(response.entity.toString)
}
