package blah.algo

case class Similarity(
  item: String,
  similarities: List[SimilarityItem] = Nil)

case class SimilarityItem(
  item: String,
  score: Double)
