package blah.referrer

import org.apache.spark.sql.types._

object ReferrerSchema {
  def apply() = StructType(Array(
    StructField("collection", StringType, true),
    StructField("props", ReferrerPropsSchema(), true)
  ))
}

object ReferrerPropsSchema {
  def apply() = StructType(Array(
    StructField("referrer", StringType, true)
  ))
}
