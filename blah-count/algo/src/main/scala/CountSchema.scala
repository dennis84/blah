package blah.count

import org.apache.spark.sql.types._

object CountSchema {
  def apply() = StructType(Array(
    StructField("date", StringType, true),
    StructField("collection", StringType, true),
    StructField("props", StructType(Array(
      StructField("item", StringType, true),
      StructField("userAgent", StringType, true),
      StructField("price", DoubleType, true)
    )), true)))
}
