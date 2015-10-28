package blah.core

case class Classification(
  mobile: Boolean = false,
  tablet: Boolean = false,
  mobileDevice: Boolean = false,
  computer: Boolean = false,
  spider: Boolean = false)

object UserAgentClassifier {
  private val mobileOsFamilies = Seq(
    "windows phone 6.5", "windows ce", "symbian os")

  private val mobileBrowsers = Seq(
    "firefox mobile", "opera mobile", "opera mini", "mobile safari", "webos",
    "ie mobile", "playstation portable", "nokia", "blackberry", "palm", "silk",
    "android", "maemo", "obigo", "netfront", "avantgo", "teleca",
    "semc-browser", "bolt", "iris", "up.browser", "symphony", "minimo",
    "bunjaloo", "jasmine", "dolfin", "polaris", "brew", "chrome mobile",
    "uc browser", "tizen browser")

  private val tablets = Seq(
    "kindle", "ipad", "playbook", "touchpad", "dell streak", "galaxy tab",
    "xoom")

  private val phones = Seq(
    "iphone", "ipod", "ipad", "htc", "kindle", "lumia", "amoi", "asus", "bird",
    "dell", "docomo", "huawei", "i-mate", "kyocera", "lenovo", "lg", "kin",
    "motorola", "philips", "samsung", "softbank", "palm", "hp",
    "generic feature phone", "generic smartphone")

  def classify(ua: UserAgent): Classification =
    if(tablets.contains(ua.device.family.toLowerCase))
      Classification(tablet = true, mobileDevice = true)
    else if(phones.contains(ua.device.family.toLowerCase))
      Classification(mobile = true, mobileDevice = true)
    else if(ua.device.family.toLowerCase == "spider")
      Classification(spider = true)
    else if(mobileOsFamilies.contains(ua.os.family.toLowerCase))
      Classification(mobile = true, mobileDevice = true)
    else if(mobileBrowsers.contains(ua.browser.family.toLowerCase))
      Classification(mobile = true, mobileDevice = true)
    else Classification(computer = true)
}
