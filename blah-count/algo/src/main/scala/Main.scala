package blah.count

import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global
import java.net.{URL, HttpURLConnection}
import org.apache.spark.SparkConf
import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]) {
    val arguments = args.map(_ split " ").flatten
    val config = ConfigFactory.load()

    val sparkConf = new SparkConf()
      .setMaster(config.getString("spark.master"))
      .setAppName("count")
    val elastiUrl = config.getString("elasticsearch.url")
    sparkConf.set("elastic.url", elastiUrl)

    val input = getClass.getResourceAsStream("/elastic_mapping.json")
    val data = Source.fromInputStream(input).mkString

    val conn = new URL(s"$elastiUrl/count").openConnection()
      .asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("PUT")
    conn.setDoOutput(true)
    conn.getOutputStream.write(data.getBytes("UTF-8"))
    conn.getOutputStream.flush()
    conn.disconnect()

    if(conn.getResponseCode >= 400) {
      val body = Source.fromInputStream(conn.getErrorStream).mkString
      if(!body.contains("index_already_exists_exception")) {
        println("Error: Put mapping request failed: " + body)
        sys exit 1
      }
    }

    arguments.lift(0) match {
      case Some("batch") =>
        BatchJob.run(config, sparkConf, arguments drop 1)
      case Some("streaming") =>
        StreamingJob.run(config, sparkConf, arguments drop 1)
      case _ =>
        println("Error: No such command: " + arguments.mkString(" "))
        sys exit 1
    }
  }
}
