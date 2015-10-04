package blah.serving

import spray.json._
import spray.json.lenses.JsonLenses._
import DefaultJsonProtocol._

object CountResponseParser {
  def parse(
    groups: List[String],
    data: JsValue
  ): Seq[JsObject] = groups match {
    case Nil => Nil
    case x :: Nil => data.extract[JsValue](x / 'buckets / *) map { bucket =>
      val count = bucket.extract[JsValue]('count / 'value)
      val key = extractKey(bucket)
      JsObject("count" -> count, x -> key)
    }
    case x :: xs => data.extract[JsValue](x / 'buckets / *) flatMap { bucket =>
      parse(xs, bucket) map {
        val key = extractKey(bucket)
        obj => JsObject(obj.fields ++ Map(x -> key))
      }
    }
  }

  private def extractKey(json: JsValue): JsValue = {
    json.extract[JsValue]('key_as_string.?) getOrElse {
      json.extract[JsValue]('key)
    }
  }
}
