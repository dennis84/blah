package blah.serving

import spray.json._
import blah.core.JsonTweaks._
import blah.elastic.FullDsl._

object UserElasticQuery {
  private def filterBy(xs: Map[String, String]): JsObject = JsObject()

  private def groupBy(xs: List[String]): JsObject =
    nest(xs.collect {
      case "country" => term("country")
    })

  def filtered(q: UserQuery): JsObject = JsObject()

  def grouped(q: UserQuery): JsObject = List(
    q.filterBy map (filterBy),
    q.groupBy map (groupBy)
  ).flatten reduceOption {
    (a,b) => a merge b
  } getOrElse JsObject()
}
