package blah.algo

import java.util.Properties
import scala.reflect.ClassTag
import scala.util.Try
import scala.language.implicitConversions
import scala.concurrent._
import scala.concurrent.duration._
import org.apache.spark.sql.Dataset
import org.apache.spark.SparkEnv
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import blah.elastic.{ElasticClient, ElasticUri}
import akka.util.ByteString

case class ElasticWriteConfig(
  url: String,
  script: String)

class DatasetElasticWriter[T: ClassTag](ds: Dataset[T]) extends Serializable {
  def writeToElastic(index: String, tpe: String) = {
    val url = SparkEnv.get.conf.get("elastic.url")
    val inline = Try(SparkEnv.get.conf.get("elastic.script"))
      .map(Some(_)).getOrElse(None)

    ds.toJSON foreachPartition { xs =>
      implicit val system = ActorSystem()
      implicit val mat = ActorMaterializer()
      import system.dispatcher

      val data = xs.flatMap(x => (parse(x), inline) match {
        case ((json, Some(id)), Some(in)) => Seq(
          update format (id, index, tpe),
          script format (in, json, json))
        case ((json, Some(id)), None) => Seq(
          update format (id, index, tpe),
          doc format (json, true))
        case ((json, None), _) => Seq(
          create format (index, tpe), json)
      }).mkString("\n") + "\n"

      val res = Http() singleRequest HttpRequest(
        method = HttpMethods.POST,
        uri = s"$url/_bulk",
        entity = HttpEntity(data))

      Await.ready(res, 10.seconds)
    }
  }

  private val update =
    """{"update":{"_id":"%s","_index":"%s","_type":"%s"}}"""
  private val create =
    """{"create":{"_index":"%s","_type":"%s"}}"""
  private val script = 
    """{"script":{"inline":"%s","lang":"groovy","params":%s},"upsert":%s}"""
  private val doc = 
    """{"doc":%s,"doc_as_upsert":%s}"""

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
