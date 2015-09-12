package blah.serving

case class ElasticUri(hosts: List[(String, Int)])

object ElasticUri {
  def apply(uri: String): ElasticUri = {
    ElasticUri(uri.substring(16).split(",").map { x =>
      val parts = x split ":"
      parts(0) -> parts(1).toInt
    }.toList)
  }
}
