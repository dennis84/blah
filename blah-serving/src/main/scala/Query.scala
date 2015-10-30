package blah.serving

case class Query(
  filterBy: Option[List[Filter]] = None,
  groupBy: Option[List[String]] = None)

case class Filter(
  val prop: String,
  val operator: String,
  val value: String)
