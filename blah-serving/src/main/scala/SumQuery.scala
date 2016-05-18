package blah.serving

case class SumQuery(
  collection: String,
  prop: String,
  filterBy: Option[List[Filter]] = None)
