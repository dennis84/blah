package blah.funnel

import org.apache.spark.sql.types._

object FunnelSchema {
  def apply() = StructType(Array(
    StructField("date", TimestampType, true),
    StructField("props", FunnelPropsSchema(), true)
  ))
}

object FunnelPropsSchema {
  def apply() = StructType(Array(
    StructField("user", StringType, true),
    StructField("item", StringType, true)
  ))
}
