package blah.serving

import spray.json._
import blah.core.JsonTweaks._

object UserElasticQuery {

  private def mergeAggs(xs: List[JsObject]): JsObject =
    xs.foldRight(JsObject()) {
      case (x, a) if a.fields.isEmpty => x
      case (x, a) => {
        val (key, value: JsObject) = x.fields.head
        JsObject(key -> (value merge JsObject("aggs" -> a)))
      }
    }

  private def filterBy(xs: Map[String, String]): JsObject = JsObject()

  private def groupBy(xs: List[String]): JsObject = JsObject(
    "aggs" -> mergeAggs(xs.collect {
      case "country" =>
        JsObject("country" -> JsObject("terms" -> JsObject("field" -> JsString("country"))))
    }
  ))

  def filtered(q: UserQuery): JsObject = JsObject()

  def grouped(q: UserQuery): JsObject = List(
    q.filterBy map (filterBy),
    q.groupBy map (groupBy)
  ).flatten reduceOption {
    (a,b) => a merge b
  } getOrElse JsObject()
}
