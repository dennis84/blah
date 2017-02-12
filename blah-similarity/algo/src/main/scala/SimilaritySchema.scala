package blah.similarity

import org.apache.spark.sql.types.{StructType, StructField, StringType}

object SimilaritySchema {
  def apply() = StructType(Array(
    StructField("collection", StringType, true),
    StructField("props", StructType(Array(
      StructField("user", StringType, true),
      StructField("item", StringType, true))), true)))
}
