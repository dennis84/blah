package blah.funnel

import org.apache.spark.sql.types.{StructType, StructField, StringType}

object FunnelSchema {
  def apply() = StructType(Array(
    StructField("date", StringType, true),
    StructField("props", StructType(Array(
      StructField("user", StringType, true),
      StructField("item", StringType, true))), true)))
}
