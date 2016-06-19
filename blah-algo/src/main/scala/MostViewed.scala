package blah.algo

case class MostViewed(
  collection: String,
  items: List[MostViewedItem] = Nil)

case class MostViewedItem(
  item: String,
  pos: Int,
  count: Int)
