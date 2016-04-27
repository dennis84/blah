package blah.algo

import scala.concurrent.ExecutionContext
import org.apache.spark.SparkConf
import com.typesafe.config.Config

class UserStreamingJob(
  name: String,
  algo: UserAlgo
) extends StreamingJob(name, algo) {
  override def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    sparkConf.set("es.update.script",
      """|ctx._source.events = ((ctx._source.events) ?
         |  ctx._source.events += events :
         |  events).takeRight(20);
         |ctx._source.date = date;
         |ctx._source.lng = lng;
         |ctx._source.lat = lat;
         |ctx._source.country = country;
         |ctx._source.countryCode = countryCode;
         |ctx._source.city = city;
         |ctx._source.zipCode = zipCode
         |""".stripMargin.replaceAll("\n", ""))
    sparkConf.set("es.update.script.params",
      """|events:events,
         |date:date,
         |lng:lng,
         |lat:lat,
         |country:country,
         |countryCode:countryCode,
         |city:city,
         |zipCode:zipCode
         |""".stripMargin.replaceAll("\n", ""))
    super.run(config, sparkConf, args)
  }
}
