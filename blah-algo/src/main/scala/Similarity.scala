package blah.algo

case class Similarity(
  item: String,
  collection: Option[String],
  similarities: List[SimilarityItem] = Nil)

case class SimilarityItem(
  item: String,
  score: Double)
