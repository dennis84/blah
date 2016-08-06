package blah.serving

import scala.util.{Try, Success, Failure}
import akka.event.Logging
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import blah.core.CorsSupport
import blah.elastic.MappingUpdater

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
    new SumService(env),
    new FunnelService(env),
    new RecommendationService(env),
    new SimilarityService(env),
    new UserService(env),
    new MostViewedService(env),
    new ReferrerService(env),
    new JobService(env))
  val routes = services.map(_.route)

  Try(env.consumer) match {
    case Failure(e) => log.warning("Unable to connect to zookeeper.")
    case Success(c) => Source.fromPublisher(c).runForeach { x =>
      (x.message.split("@", 2)).toList match {
        case e +: m +: Nil => env.websocketHub ! (e, m)
        case _ => println("Could not handle message")
      }
    }
  }

  env.indexUpdater.update("blah", env.elasticIndex) onComplete {
    case Success(MappingUpdater.Created(index)) =>
      log.debug(s"Successfully initialized elasticsearch index (index: $index)")
    case Success(MappingUpdater.Skipped(index)) =>
      log.debug(s"Current elasticsearch index is up to date (index: $index)")
    case Success(MappingUpdater.Updated(index)) =>
      log.debug(s"Successfully updated elasticsearch index to a new version (index: $index)")
    case Failure(e) =>
      log.error(s"Index update failed: ${e.getMessage}")
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
