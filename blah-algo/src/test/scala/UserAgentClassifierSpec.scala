package blah.algo

import org.scalatest._

class UserAgentClassifierSpec extends FlatSpec with Matchers {

  "The UserAgentClassifier" should "classify an empty UA" in {
    UserAgentClassifier.classify(UserAgent(
      Browser("Other"),
      OS("Other"),
      Device("Other")
    )) should be (Classification(computer = true))
  }

  it should "classify an UA with browser" in {
    UserAgentClassifier.classify(UserAgent(
      Browser("Chrome Mobile"),
      OS("Other"),
      Device("Other")
    )) should be (Classification(mobile = true, mobileDevice = true))
  }

  it should "classify an UA with device" in {
    UserAgentClassifier.classify(UserAgent(
      Browser("Other"),
      OS("Other"),
      Device("iPhone")
    )) should be (Classification(mobile = true, mobileDevice = true))
  }

  it should "classify an BlackBerry" in {
    UserAgentClassifier.classify(UserAgent(
      Browser("Safari", None, None, None),
      OS("BlackBerry OS", None, None, None),
      Device("BlackBerry")
    )) should be (Classification(mobile = true, mobileDevice = true))
  }
}
