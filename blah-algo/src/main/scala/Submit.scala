package blah.algo

object Submit {
  def main(args: Array[String]) {
    val algos = Map(
      "count" -> new CountAlgo,
      "similarity" -> new SimilarityAlgo)
    val algo = algos(args(0))
    algo.train
  }
}
