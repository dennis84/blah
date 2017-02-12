package blah.count

case class Count(
  id: Option[String] = None,
  collection: String,
  date: String,
  item: Option[String] = None,
  browserFamily: Option[String] = None,
  browserMajor: Option[String] = None,
  osFamily: Option[String] = None,
  osMajor: Option[String] = None,
  deviceFamily: Option[String] = None,
  isMobile: Option[Boolean] = None,
  isTablet: Option[Boolean] = None,
  isMobileDevice: Option[Boolean] = None,
  isComputer: Option[Boolean] = None,
  platform: Option[String] = None,
  price: Option[Double] = None,
  count: Long = 0)
