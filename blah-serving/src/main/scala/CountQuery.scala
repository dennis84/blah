package blah.serving

case class CountQuery(
  collection: String,
  filterBy: Option[List[Filter]] = None,
  groupBy: Option[List[String]] = None)
