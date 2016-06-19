package blah.algo

import org.apache.spark.sql.types.{StructType, StructField, StringType}

object MostViewedSchema {
  def apply() = StructType(Array(
    StructField("date", StringType, true),
    StructField("collection", StringType, true),
    StructField("props", StructType(Array(
      StructField("item", StringType, true))), true)))
}
