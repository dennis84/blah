package blah.funnel

import org.apache.spark.sql.Row

case class FunnelEvent(
  date: String,
  user: String,
  item: String)
