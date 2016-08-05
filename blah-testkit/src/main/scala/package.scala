package blah

import java.io.IOException
import java.net.{InetSocketAddress, Socket}

package object testkit {
  def isReachable(
    host: String,
    port: Int,
    timeout: Int = 2000
  ): Boolean = {
    val socket = new Socket
    try {
      socket.connect(new InetSocketAddress(host, port), timeout)
      true
    } catch {
      case e: IOException => false
    } finally {
      socket.close()
    }
  }
}
