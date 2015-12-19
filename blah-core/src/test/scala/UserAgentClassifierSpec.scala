package blah.core

import org.scalatest._

class UserAgentClassifierSpec extends FlatSpec with Matchers {

  "UserAgentClassifier" should "classify other" in {
    UserAgentClassifier.classify(UserAgent(
      Browser("Other"),
      OS("Other"),
      Device("Other")
    )) should be(Classification(computer = true))
  }

  it should "classify browser" in {
    UserAgentClassifier.classify(UserAgent(
      Browser("Chrome Mobile"),
      OS("Other"),
      Device("Other")
    )) should be(Classification(mobile = true, mobileDevice = true))
  }

  it should "classify device" in {
    UserAgentClassifier.classify(UserAgent(
      Browser("Other"),
      OS("Other"),
      Device("iPhone")
    )) should be(Classification(mobile = true, mobileDevice = true))
  }
}
