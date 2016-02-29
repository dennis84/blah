package blah.core

import org.scalatest._
import FindOpt._

class FindOptSpec extends FlatSpec with Matchers {
  "With FindOpt in scope" should "find opts" in {
    val args = Array("--a", "foo", "--b", "bar", "--c", "--d")
    args opt "a" should be (Some("foo"))
    args opt "b" should be (Some("bar"))
    args opt "c" should be (Some(""))
    args opt "d" should be (Some(""))
  }

  it should "shift opts" in {
    val args = Array("--a", "foo", "--b", "bar", "--c", "--d")
    args shift "a" should be (Array("--b", "bar", "--c", "--d"))
    args shift "b" should be (Array("--a", "foo", "--c", "--d"))
    args shift "c" should be (Array("--a", "foo", "--b", "bar", "--d"))
    args shift "d" should be (Array("--a", "foo", "--b", "bar", "--c"))
  }
}
