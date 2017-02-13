package blah.collection

import org.apache.spark.sql.types._

object CollectionCountSchema {
  def apply() = StructType(Array(
    StructField("date", TimestampType, true),
    StructField("collection", StringType, true)
  ))
}
