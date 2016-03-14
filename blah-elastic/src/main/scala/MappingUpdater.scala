package blah.elastic

import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import spray.json.lenses.JsonLenses._
import blah.core.JsonDsl._

class IndexUpdater(client: ElasticClient)(
  implicit system: ActorSystem,
  mat: Materializer
) extends SprayJsonSupport with DefaultJsonProtocol {
  import system.dispatcher
  import IndexUpdater._

  private def getIndex(index: String) =
    client request HttpRequest(
      method = HttpMethods.GET,
      uri = s"/$index/")

  private def createIndex(index: String, data: JsObject) =
    client request HttpRequest(
      method = HttpMethods.PUT,
      uri = s"/$index/",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        data.compactPrint))

  private def aliases(actions: List[JsObject]) =
    client request HttpRequest(
      method = HttpMethods.POST,
      uri = "_aliases",
      entity = HttpEntity(
        ContentTypes.`application/json`,
        ("actions" -> actions).compactPrint))

  private def shouldUpdate(curr: JsObject, data: JsObject) =
    data.fields.get("mappings")
      .map(curr.extract[JsObject]('mappings) != _)
      .getOrElse(false) ||
    data.fields.get("settings")
      .map(curr.extract[JsObject]('settings / 'index) notContains _.asJsObject)
      .getOrElse(false)

  def update(index: String, data: JsObject): Future[Result] =
    getIndex(index) flatMap {
      case HttpResponse(StatusCodes.NotFound, _, _, _) => for {
        r <- createIndex(s"$index-1", data)
        _ = if(r.status.isFailure) throw UpdateFailed(r)
        r <- aliases(List(
          ("add" -> ("index" -> s"$index-1") ~ ("alias" -> index))))
        _ = if(r.status.isFailure) throw UpdateFailed(r)
      } yield Created(s"$index-1")

      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        Unmarshal(entity).to[JsObject] flatMap { json =>
          val pattern = """.*-(\d+)""".r
          json.fields.toList match {
            case (currentIndex, currentData) :: Nil
                if(!shouldUpdate(currentData.asJsObject, data)) =>
              Future(Skipped(currentIndex))

            case (currentIndex@pattern(x), _) :: Nil =>
              val nextIndex = x.toInt + 1
              for {
                r <- createIndex(s"$index-$nextIndex", data)
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

object IndexUpdater {
  sealed trait Result
  case class Created(index: String) extends Result
  case class Updated(index: String) extends Result
  case class Skipped(index: String) extends Result
  case class UpdateFailed(val response: HttpResponse)
    extends Exception(response.entity.toString)
}
