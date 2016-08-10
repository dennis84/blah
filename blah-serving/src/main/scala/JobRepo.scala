package blah.serving

import scala.concurrent._
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import blah.core.{HttpClient, Message}

class JobRepo(client: HttpClient)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  mat: Materializer
) extends SprayJsonSupport with ServingJsonProtocol {
  import system.dispatcher

  def list(): Future[List[Job]] =
    for {
      jobsResp <- client request HttpRequest(GET, "/scheduler/jobs")
      chronosJobs <- Unmarshal(jobsResp.entity).to[List[ChronosJob]]
      csvResp <- client request HttpRequest(GET, "/scheduler/graph/csv")
      csv <- Unmarshal(csvResp.entity).to[String]
      csvData = csv.lines.collect(_ split "," match {
        case Array(_, name, last, status) => name -> status
      }).toMap
    } yield chronosJobs map { chronosJob =>
      chronosJob.toJob(csvData get chronosJob.name)
    }

  def run(name: String): Future[Message] =
    client request HttpRequest(
      method = HttpMethods.PUT,
      uri = s"/scheduler/job/$name"
    ) collect {
      case r if r.status.isSuccess =>
        Message("Job has started successfully.")
      case r if r.status == BadRequest =>
        Message("Job could not be started.")
    }

  def stop(name: String): Future[Message] =
    client request HttpRequest(
      method = HttpMethods.DELETE,
      uri = s"/scheduler/task/kill/$name"
    ) collect {
      case r if r.status.isSuccess =>
        Message("Job was successfully stopped.")
    }
}
