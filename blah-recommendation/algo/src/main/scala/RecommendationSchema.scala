package blah.recommendation

import org.apache.spark.sql.types._

object RecommendationSchema {
  def apply() = StructType(Array(
    StructField("collection", StringType, true),
    StructField("props", RecommendationPropsSchema())
  ))
}

object RecommendationPropsSchema {
  def apply() = StructType(Array(
    StructField("user", StringType, true),
    StructField("item", StringType, true)
  ))
}
