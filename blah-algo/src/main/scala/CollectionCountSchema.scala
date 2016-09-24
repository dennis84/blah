package blah.algo

import org.apache.spark.sql.types.{StructType, StructField, StringType}

object CollectionCountSchema {
  def apply() = StructType(Array(
    StructField("collection", StringType, true)
  ))
}
