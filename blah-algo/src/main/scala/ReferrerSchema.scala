package blah.algo

import org.apache.spark.sql.types.{StructType, StructField, StringType}

object ReferrerSchema {
  def apply() = StructType(Array(
    StructField("props", StructType(Array(
      StructField("referrer", StringType, true))), true)))
}
