package blah.test

import org.scalatest._
import org.scalatest.selenium._

class UserSpec extends FlatSpec with Matchers with Chrome {

  val host = "http://ui.blah.local/"

  "User features" should "work" in {
    go to (host)
    pageTitle should be ("Blah")
    quit()
  }
}
