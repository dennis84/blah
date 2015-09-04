package blah.serving

object QueryToCQL {

  private def where(key: String, value: String): String = key match {
    case "page"                      => s"name = '$value'"
    case "date.from"                 => s"date >= '$value'"
    case "date.to"                   => s"date <= '$value'"
    case "user_agent.device.family"  => s"device_family = '$value'"
    case "user_agent.browser.family" => s"browser_family = '$value'"
    case "user_agent.browser.major"  => s"browser_major = '$value'"
    case "user_agent.browser.minor"  => s"browser_minor = '$value'"
    case "user_agent.os.family"      => s"os_family = '$value'"
    case "user_agent.os.major"       => s"os_major = '$value'"
    case "user_agent.os.minor"       => s"os_minor = '$value'"
    case _ => key
  }

  private def table(q: Query): String = {
    q.filterBy.map(_.collectFirst {
      case(k,_) if k.startsWith("user_agent.device")  => "count_by_device"
      case(k,_) if k.startsWith("user_agent.browser") => "count_by_browser"
      case(k,_) if k.startsWith("user_agent.os")      => "count_by_os"
    }).flatten.getOrElse("count_by_date")
  }

  def apply(q: Query) = {
    val w = q.filterBy map (_.foldLeft("") {
      case ("", (k, v)) => s"where ${where(k, v)}"
      case (a, (k, v)) => s"$a and ${where(k, v)}"
    }) getOrElse ""

    val cql = s"""|select count from blah.${table(q)}
                  |$w allow filtering
                  |;""".stripMargin
    println(cql)
    cql
  }
}
