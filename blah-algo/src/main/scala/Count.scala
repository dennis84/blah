package blah.algo

case class Count(
  collection: String,
  date: String,
  item: String,
  browserFamily: String,
  browserMajor: String,
  osFamily: String,
  osMajor: String,
  deviceFamily: String,
  isMobile: Boolean,
  isTablet: Boolean,
  isMobileDevice: Boolean,
  isComputer: Boolean,
  platform: String,
  count: Long = 0)
