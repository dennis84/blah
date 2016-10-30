package blah.serving

import spray.json._
import blah.json.JsonDsl._
import blah.elastic.{QueryDsl => q}
import blah.elastic.{FilterDsl => f}
import blah.elastic.{AggregationDsl => a}

object SumElasticQuery {
  private def filterBy(xs: List[Filter]) = xs collect {
    case Filter("date.from", "gte", value) => f.gte("date", value)
    case Filter("date.to", "lte", value)   => f.lte("date", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  private def sum(prop: String) =
    ("aggs" -> ("sum" -> ("sum" ->
      ("script" -> s"doc['$prop'].value * doc['count'].value")
    )))

  def apply(query: SumQuery): JsValue =
    q.term("collection", query.collection) merge (query match {
      case SumQuery(_, prop, Some(filters)) =>
        filterBy(filters) merge sum(prop)
      case SumQuery(_, prop, _) => sum(prop)
    })
}
