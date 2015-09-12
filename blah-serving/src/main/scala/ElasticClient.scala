package blah.serving

import scala.util.Try
import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
 
class ElasticClient(uri: ElasticUri)(
  implicit system: ActorSystem,
  mat: Materializer) {
  import system.dispatcher

  private val flow =
    Http().newHostConnectionPool[Any](
      uri.hosts.head._1,
      uri.hosts.head._2)

  def request(req: HttpRequest) =
    Source.single(req -> null)
      .via(flow)
      .runWith(Sink.head)
      .map(_._1.get)
}
