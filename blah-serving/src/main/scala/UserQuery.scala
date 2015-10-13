package blah.serving

case class UserQuery(
  filterBy: Option[Map[String, String]] = None,
  groupBy: Option[List[String]] = None)
