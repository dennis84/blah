package blah.serving

import spray.json._
import JsonDsl._

object SumElasticQuery {
  private def filterBy(xs: List[Filter]) = xs collect {
    case Filter("date.from", "gte", value) => FilterDsl.gte("date", value)
    case Filter("date.to", "lte", value)   => FilterDsl.lte("date", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  private def sum(prop: String) =
    ("aggs" -> ("sum" -> ("sum" ->
      ("script" -> s"doc['$prop'].value * doc['count'].value")
    )))

  def apply(query: SumQuery): JsValue =
    QueryDsl.term("collection", query.collection) merge (query match {
      case SumQuery(_, prop, Some(filters)) =>
        filterBy(filters) merge sum(prop)
      case SumQuery(_, prop, _) => sum(prop)
    })
}
