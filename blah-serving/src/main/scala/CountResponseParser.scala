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
      val count = bucket.extract[JsValue]('doc_count)
      val key = bucket.extract[JsValue]('key)
      JsObject("count" -> count, x -> key)
    }
    case x :: xs => data.extract[JsValue](x / 'buckets / *) flatMap { bucket =>
      parse(xs, bucket) map {
        val key = bucket.extract[JsValue]('key)
        obj => JsObject(obj.fields ++ Map(x -> key))
      }
    }
  }
}
