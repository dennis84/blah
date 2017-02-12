package blah.count

import java.util.Properties
import java.net.{URL, HttpURLConnection}
import scala.reflect.ClassTag
import scala.util.Try
import scala.language.implicitConversions
import org.apache.spark.sql.Dataset
import org.apache.spark.SparkEnv

case class ElasticWriteConfig(
  url: String,
  script: String)

class DatasetElasticWriter[T: ClassTag](ds: Dataset[T]) extends Serializable {
  def writeToElastic(index: String, tpe: String) = {
    val url = SparkEnv.get.conf.get("elastic.url")
    val inline = Try(SparkEnv.get.conf.get("elastic.script"))
      .map(Some(_)).getOrElse(None)

    ds.toJSON foreachPartition { xs =>
      val data = xs.flatMap(x => (parse(x), inline) match {
        case ((json, Some(id)), Some(in)) => Seq(
          update format (id, index, tpe),
          script format (in, json, json))
        case ((json, Some(id)), None) => Seq(
          update format (id, index, tpe),
          doc format json)
        case ((json, Some(id)), _) => Seq(
          create format (index, tpe), json)
        case (_, _) => Nil
      }).mkString("\n") + "\n"

      val conn = new URL(s"$url/_bulk").openConnection()
        .asInstanceOf[HttpURLConnection]
      conn.setRequestMethod("POST")
      conn.setDoOutput(true)
      conn.getOutputStream().write(data.getBytes("UTF-8"))
      conn.getOutputStream().flush()
      conn.getResponseCode()
      conn.disconnect()
    }
  }

  private val update =
    """{"update":{"_id":"%s","_index":"%s","_type":"%s"}}"""
  private val create =
    """{"create":{"_index":"%s","_type":"%s"}}"""
  private val script = 
    """{"script":{"inline":"%s","lang":"groovy","params":{"params":%s}},"upsert":%s}"""
  private val doc = 
    """{"doc":%s,"doc_as_upsert":true}"""

  private def parse(json: String): (String, Option[String]) = {
    val pat1 = """.*"id":"(.*?)",.*""".r
    val pat2 = """"id":".*?",""".r
    json match {
      case pat1(id) => (pat2.replaceAllIn(json, ""), Some(id))
      case _ => (json, None)
    }
  }
}

object DatasetElasticWriter {
  implicit def elasticDs[T: ClassTag](ds: Dataset[T]) =
    new DatasetElasticWriter(ds)
}
