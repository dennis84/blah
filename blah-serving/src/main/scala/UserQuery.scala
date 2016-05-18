package blah.serving

case class UserQuery(
  filterBy: Option[List[Filter]] = None,
  groupBy: Option[List[String]] = None)
