package blah.similarity

case class Similarity(
  id: String,
  item: String,
  collection: Option[String] = None,
  similarities: Seq[SimilarityItem] = Nil)

case class SimilarityItem(
  item: String,
  score: Double)
