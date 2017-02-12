package blah.serving

import scala.concurrent._
import scala.util.{Success, Failure}
import akka.actor.ActorSystem
import akka.stream.{Materializer, OverflowStrategy, QueueOfferResult}
import akka.stream.scaladsl._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

class HttpClient(host: String, port: Int)(
  implicit system: ActorSystem,
  mat: Materializer) {
  import system.dispatcher

  private lazy val flow =
    Http().cachedHostConnectionPool[Promise[HttpResponse]](host, port)

  private lazy val queue =
    Source.queue[(HttpRequest, Promise[HttpResponse])](
      1000, OverflowStrategy.backpressure)
        .via(flow)
        .toMat(Sink foreach {
          case (Success(r), p) => p success r
          case (Failure(e), p) => p failure e
        })(Keep.left)
        .run

  def request(req: HttpRequest): Future[HttpResponse] = {
    val promise = Promise[HttpResponse]
    val request = req -> promise
    (queue offer request) flatMap {
      case QueueOfferResult.Enqueued => promise.future
      case _ => Future.failed(new RuntimeException(
        "Failed to queue request."))
    }
  }
}
