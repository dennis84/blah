package blah.funnel

import java.sql.Timestamp
import org.apache.spark.sql.Row

case class FunnelEvent(
  date: Timestamp,
  user: String,
  item: String)
