package blah.serving

case class CountQuery(
  filterBy: Option[Map[String, String]] = None,
  groupBy: Option[List[String]] = None)
