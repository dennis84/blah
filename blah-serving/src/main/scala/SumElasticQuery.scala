package blah.serving

import spray.json._
import blah.core.JsonDsl._
import blah.elastic.{QueryDsl => q}
import blah.elastic.{FilterDsl => f}
import blah.elastic.{AggregationDsl => a}
import blah.elastic.AggregationMerge._

object SumElasticQuery {
  private def filterBy(xs: List[Filter]) = xs collect {
    case Filter("date.from", "gte", value) => f.gte("date", value)
    case Filter("date.to", "lte", value)   => f.lte("date", value)
  } reduceOption (_ merge _) getOrElse JsObject()

  def apply(query: SumQuery): JsValue =
    q.term("collection", query.collection) merge (query match {
      case SumQuery(_, prop, Some(filters)) =>
        filterBy(filters) merge a.sum("sum", prop)
      case SumQuery(_, prop, _) =>
        a.sum("sum", prop)
    })
}
