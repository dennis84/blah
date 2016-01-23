package blah.serving

case class UserQuery(
  filterBy: Option[List[Filter]] = None,
  groupBy: Option[List[String]] = None)

case class CountQuery(
  collection: String,
  filterBy: Option[List[Filter]] = None,
  groupBy: Option[List[String]] = None)

case class SumQuery(
  collection: String,
  prop: String,
  filterBy: Option[List[Filter]] = None)

case class Filter(
  val prop: String,
  val operator: String,
  val value: String)
