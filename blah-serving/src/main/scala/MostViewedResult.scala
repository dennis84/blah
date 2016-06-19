package blah.serving

case class MostViewedResult(
  collection: String,
  items: List[MostViewedItem] = Nil)

case class MostViewedItem(
  item: String,
  pos: Int,
  count: Int)