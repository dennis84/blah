package blah.elastic

import akka.actor.ActorSystem
import akka.stream.Materializer
import blah.core.HttpClient

class ElasticClient(uri: ElasticUri)(
  implicit system: ActorSystem,
  mat: Materializer
) extends HttpClient(uri.hosts.head._1, uri.hosts.head._2)
