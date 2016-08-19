package blah.serving

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

class JobService(env: Env)(
  implicit system: ActorSystem,
  log: LoggingAdapter,
  materializer: Materializer
) extends Service with JobJsonFormat with SprayJsonSupport {
  import system.dispatcher

  private val repo = new JobRepo(env.chronosClient)

  def route =
    // Listing all jobs
    (get & path("jobs")) {
      complete(repo.list)
    } ~
    // Starting a job
    (put & path("jobs" / Segment)) { name =>
      complete(repo.run(name))
    } ~
    // Stopping a job
    (delete & path("jobs" / Segment)) { name =>
      complete(repo.stop(name))
    }
}
