package blah.count

import scala.concurrent.ExecutionContext.Implicits.global
import org.apache.spark.SparkConf
import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]) {
    val arguments = args.map(_ split " ").flatten
    val config = ConfigFactory.load()

    val sparkConf = new SparkConf()
      .setMaster(config.getString("spark.master"))
      .setAppName("count")
    sparkConf.set("elastic.url", config.getString("elasticsearch.url"))

    arguments.lift(0) match {
      case Some("batch") =>
        BatchJob.run(config, sparkConf, arguments drop 1)
      case Some("streaming") =>
        StreamingJob.run(config, sparkConf, arguments drop 1)
      case _ =>
        println(s"""|Error: No such command: ${arguments.mkString(" ")}
                    |Usage: java -jar algo.jar COMMAND [OPTION]
                    |Commands:
                    |  batch
                    |  streaming
                    |""".stripMargin)
        sys exit 1
    }
  }
}
