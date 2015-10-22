package blah.elastic

import scala.util.{Try, Success, Failure}
import spray.json._
import spray.json.lenses.JsonLenses._
import DefaultJsonProtocol._

object AggregationParser {
  
  private def extractKey(json: JsValue): JsValue =
    json.extract[JsValue]('key_as_string.?) getOrElse json.extract[JsValue]('key)

  private def extractCount(json: JsValue): JsValue =
    json.extract[JsValue]('count.? / 'value) getOrElse json.extract[JsValue]('doc_count)

  private def parseBucket(key: String, data: JsObject): List[JsObject] = {
    val value = extractKey(data)
    val count = extractCount(data)
    data.fields.toList collectFirst {
      case (k, v: JsObject) if v.fields.contains("buckets") => parse(JsObject(k -> v))
    } map (_ map {
      obj => JsObject(Map(key -> value) ++ obj.fields)
    }) getOrElse List(JsObject(key -> value, "count" -> count))
  }

  def parse(data: JsValue): List[JsObject] = data match {
    case JsObject(fields) if fields.isEmpty => Nil
    case JsObject(fields) =>
      val (key, value) = fields.head
      value.extract[JsObject]('buckets / *).toList flatMap {
        bucket => parseBucket(key, bucket)
      }
    case _ => Nil
  }

  def parseTo[A: JsonReader](data: JsValue): List[A] =
    parse(data) map (_.convertTo[A])
}
