package blah.serving

case class CountResult(count: Long)

case class CountAllResult(
  views: List[PageView] = Nil)

case class PageView(
  name: String,
  count: Long)
