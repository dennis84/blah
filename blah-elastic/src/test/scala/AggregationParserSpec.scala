package blah.elastic

import org.scalatest._
import spray.json._
import blah.json.JsonDsl._

class AggregationParserSpec extends FlatSpec with Matchers {

  val flat: JsObject =
    ("country" -> ("buckets" -> List(
      ("key" -> "Germany") ~ ("doc_count" -> 3),
      ("key" -> "United States") ~ ("doc_count" -> 2))))

  val nested: JsObject =
    ("date" -> ("buckets" -> List(
      ("key_as_string" -> "2015-09-04") ~
      ("doc_count" -> 3) ~
      ("browserFamily.keyword" ->
        ("doc_count_error_upper_bound" -> 0) ~
        ("sum_other_doc_count" -> 0) ~
        ("buckets" -> List(
          ("key" -> "Chrome") ~
          ("doc_count" -> 4) ~
          ("count" -> ("value" -> 5)),
          ("key" -> "Firefox") ~
          ("doc_count" -> 6) ~
          ("count" -> ("value" -> 7))))),
      ("key_as_string" -> "2015-09-05") ~
      ("doc_count" -> 3) ~
      ("browserFamily.keyword" ->
        ("doc_count_error_upper_bound" -> 0) ~
        ("sum_other_doc_count" -> 0) ~
        ("buckets" -> List(
          ("key" -> "Chrome") ~
          ("doc_count" -> 4) ~
          ("count" -> ("value" -> 10)),
          ("key" -> "Firefox") ~
          ("doc_count" -> 6) ~
          ("count" -> ("value" -> 14)))))
    )))

  "The AggregationParser" should "parse a flat response" in {
    AggregationParser.parse(flat) should be (List(
      ("count" -> 3) ~ ("country" -> "Germany"),
      ("count" -> 2) ~ ("country" -> "United States")))
  }

  it should "parse a nested response" in {
    AggregationParser.parse(nested) should be (List(
      ("count" -> 5) ~
      ("date" -> "2015-09-04") ~
      ("browserFamily" -> "Chrome"),
      ("count" -> 7) ~
      ("date" -> "2015-09-04") ~
      ("browserFamily" -> "Firefox"),
      ("count" -> 10) ~
      ("date" -> "2015-09-05") ~
      ("browserFamily" -> "Chrome"),
      ("count" -> 14) ~
      ("date" -> "2015-09-05") ~
      ("browserFamily" -> "Firefox")
    ))
  }
}
