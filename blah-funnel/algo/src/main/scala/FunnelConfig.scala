package blah.funnel

case class FunnelConfig(
  name: String,
  steps: List[String] = Nil)
