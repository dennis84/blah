package blah.funnel

trait FindOpt {
  implicit class FindOptInArray(a: Array[String]) {
    def opt(name: String): Option[String] = {
      val index = a indexOf s"--$name"
      if(index >= 0) a lift (index + 1) match {
        case Some(x) if x startsWith "--" => Some("")
        case Some(x)                      => Some(x)
        case None                         => Some("")
      } else None
    }

    def shift(name: String): Array[String] = {
      val index = a indexOf s"--$name"
      if(index >= 0) a lift (index + 1) match {
        case Some(x) if x startsWith "--" => a patch (index, Nil, 1)
        case Some(x)                      => a patch (index, Nil, 2)
        case None                         => a patch (index, Nil, 1)
      } else a
    }
  }
}

object FindOpt extends FindOpt
