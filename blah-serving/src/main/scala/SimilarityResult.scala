package blah.serving

case class SimilarityResult(
  user: String,
  views: Map[String, Double] = Map.empty)
