package blah.similarity

import org.apache.spark.sql.types._

object SimilaritySchema {
  def apply() = StructType(Array(
    StructField("collection", StringType, true),
    StructField("props", SimilarityPropsSchema(), true)
  ))
}

object SimilarityPropsSchema {
  def apply() = StructType(Array(
    StructField("user", StringType, true),
    StructField("item", StringType, true)
  ))
}
