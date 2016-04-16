package blah.algo

case class Similarity(
  user: String,
  views: List[SimilarityItem] = Nil)

case class SimilarityItem(
  item: String,
  score: Double)
