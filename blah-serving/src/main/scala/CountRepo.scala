package blah.serving

import scala.concurrent._
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import spray.json._
import ServingJsonProtocol._
import spray.json.lenses.JsonLenses._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.unmarshalling.Unmarshal

class CountRepo(implicit system: ActorSystem, mat: Materializer) extends SprayJsonSupport {
  import system.dispatcher

  def query(q: Query): Future[CountResult] = {
    val resp: Future[SimilarityResult] = Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:9200/blah/sims/_search",
      entity = HttpEntity(ContentTypes.`application/json`, Map(
        "query" -> Map("term" -> Map("user" -> "user1"))
      ).toJson.compactPrint)
    )).flatMap(resp => Unmarshal(resp.entity).to[JsValue]).map { json =>
      val views = 'hits / 'hits / * / '_source
      json.extract[SimilarityResult](views).head
    }
    
    Future(CountResult(10L))
  }

    // conn.executeAsync(QueryToCQL(q)) map { xs =>
    //   CountResult(xs.all.map(_.getLong("count")).sum)
    // }

//   def count(q: CountQuery): Future[CountResult] = {
//     val where = List(
//       q.page.map(x => s"name='$x'"),
//       q.from.map(x => s"date >= '${x.getMillis}'"),
//       q.to.map(x => s"date <= '${x.getMillis}'")
//     ).flatten.foldLeft("") {
//       case ("", x) => "where " + x
//       case (a, x)  => a + " and " + x
//     }
//     val cql = s"""|select count from blah.count
//                   |$where allow filtering
//                   |;""".stripMargin
//     conn.executeAsync(cql) map { xs =>
//       CountResult(xs.all.map(_.getLong("count")).sum)
//     }
//   }

//   def countAll(q: CountAllQuery): Future[CountAllResult] = {
//     val where = List(
//       q.from.map(x => s"date >= '${x.getMillis}'"),
//       q.to.map(x => s"date <= '${x.getMillis}'")
//     ).flatten.foldLeft("") {
//       case ("", x) => "where " + x
//       case (a, x)  => a + " and " + x
//     }
//     val cql = s"""|select name, count from blah.count
//                   |$where allow filtering
//                   |;""".stripMargin
//     conn.executeAsync(cql) map { xs =>
//       val ys = xs.map(x => (x.getString("name"), x.getLong("count")))
//       val zs = ys.groupBy(_._1).map(x => PageView(x._1, x._2.map(_._2).sum)).toList
//       CountAllResult(zs)
//     }
//   }
}
