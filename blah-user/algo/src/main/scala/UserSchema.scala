package blah.user

import org.apache.spark.sql.types._

object UserSchema {
  def apply() = StructType(Array(
    StructField("date", TimestampType, true),
    StructField("collection", StringType, true),
    StructField("props", UserPropsSchema(), true)
  ))
}

object UserPropsSchema {
  def apply() = StructType(Array(
    StructField("user", StringType, true),
    StructField("email", StringType, true),
    StructField("firstname", StringType, true),
    StructField("lastname", StringType, true),
    StructField("item", StringType, true),
    StructField("title", StringType, true),
    StructField("ip", StringType, true)
  ))
}
