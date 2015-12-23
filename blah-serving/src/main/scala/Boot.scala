package blah.serving

import scala.util.{Try, Success, Failure}
import akka.event.Logging
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import blah.elastic.Mapping

object Boot extends App
  with CorsSupport
  with ExceptionHandling
  with RejectionHandling {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val log = Logging.getLogger(system, this)

  val config = system.settings.config
  val interface = config.getString("app.interface")
  val port = config.getInt("app.port")
  val env = new Env
  val services = Seq(
    new StatusService,
    new WebsocketService(env.websocketRoom),
    new CountService(env),
    new SimilarityService(env),
    new UserService(env))
  val routes = services.map(_.route)

  Try(env.consumer) match {
    case Success(c) => Source(c).runForeach(x => env.websocketHub ! x.message)
    case Failure(e) => println("Unable to connect to zookeeper.")
  }

  env.mappingUpdater.update("blah", env.elasticMapping) onSuccess {
    case Mapping.Created(index) =>
      println(s"Successfully initialized elasticsearch mapping (index: $index)")
    case Mapping.Skipped(index) =>
      println(s"Current elasticsearch mapping is up to date (index: $index)")
    case Mapping.Updated(index) =>
      println(s"Successfully updated elasticsearch mapping to a new version (index: $index)")
  }

  (for {
    head <- routes.headOption
    tail = routes.tail
  } yield (head /: tail) {
    case (xs, x) => xs ~ x
  }) map { route =>
    Http().bindAndHandle(corsHandler(route), interface, port)
  }
}
