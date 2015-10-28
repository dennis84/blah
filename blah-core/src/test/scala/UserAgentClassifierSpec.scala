package blah.core

import org.scalatest._

class UserAgentClassifierSpec extends FlatSpec with Matchers {

  "UserAgentClassifier" should "classify" in {
    UserAgentClassifier.classify(UserAgent(
      Browser("Other"),
      OS("Other"),
      Device("Other")
    )) should be(Classification(computer = true))

    UserAgentClassifier.classify(UserAgent(
      Browser("Chrome Mobile"),
      OS("Other"),
      Device("Other")
    )) should be(Classification(mobile = true, mobileDevice = true))

    UserAgentClassifier.classify(UserAgent(
      Browser("Other"),
      OS("Other"),
      Device("iPhone")
    )) should be(Classification(mobile = true, mobileDevice = true))
  }
}
