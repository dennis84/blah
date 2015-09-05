package blah.serving

import scala.concurrent._

class SimilarityRepo(implicit ec: ExecutionContext) {

  def sims(q: SimilarityQuery): Future[SimilarityResult] = {
    Future(SimilarityResult("user-1"))
    // val cql = s"""|select * from blah.sims
    //               |where user='${q.user}'
    //               |;""".stripMargin
    // conn.executeAsync(cql) map { res =>
    //   val views: Map[String, Double] =
    //     if(!res.isExhausted)
    //       res.one.getMap("views", classOf[String], classOf[java.lang.Double])
    //         .asScala.toMap.map(x => x._1 -> x._2.doubleValue)
    //     else Map.empty
    //   SimilarityResult(q.user,
    //       q.limit map (x => views take x.toInt) getOrElse views)
    // }
  }
}
