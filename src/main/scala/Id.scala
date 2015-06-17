package blah

import scala.util.Random

object Id {

  private val chars: IndexedSeq[Char] = (('0' to '9') ++ ('a' to 'z'))
  private val nbChars = chars.size

  private def nextString(len: Int) = List.fill(len)(nextChar).mkString
  private def nextChar = chars(Random nextInt nbChars)

  def generate = nextString(8)
}
