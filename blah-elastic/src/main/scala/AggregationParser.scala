package blah.elastic

import spray.json._
import spray.json.lenses.JsonLenses._
import blah.json.JsonDsl._
import DefaultJsonProtocol._

object AggregationParser {

  private def extractKey(json: JsValue) =
    json.extract[JsValue]('key_as_string.?) getOrElse {
      json.extract[JsValue]('key)
    }

  private def extractCount(json: JsValue) =
    json.extract[JsValue]('count.? / 'value) getOrElse {
      json.extract[JsValue]('doc_count)
    }

  private def parseBucket(key: String, data: JsObject) = {
    val value = extractKey(data)
    val count = extractCount(data)
    val maybeBuckets = data.fields.toList collectFirst {
      case (k, v: JsObject) if v.fields.contains("buckets") => parse(k -> v)
    }

    maybeBuckets map { xs =>
      xs map (_ merge (key -> value))
    } getOrElse {
      List(JsObject(key -> value, "count" -> count))
    }
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
