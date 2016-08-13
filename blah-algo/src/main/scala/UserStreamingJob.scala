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
    sparkConf.set("elastic.script",
      """|ctx._source.events = (ctx._source.events += params.events).takeRight(20);
         |ctx._source.nbEvents += params.nbEvents;
         |ctx._source.date = params.date;
         |if(params.email) ctx._source.email = params.email;
         |if(params.firstname) ctx._source.firstname = params.firstname;
         |if(params.lastname) ctx._source.lastname = params.lastname;
         |if(params.lng) ctx._source.lng = params.lng;
         |if(params.lat) ctx._source.lat = params.lat;
         |if(params.country) ctx._source.country = params.country;
         |if(params.countryCode) ctx._source.countryCode = params.countryCode;
         |if(params.city) ctx._source.city = params.city;
         |if(params.zipCode) ctx._source.zipCode = params.zipCode
         |""".stripMargin.replaceAll("\n", ""))
    super.run(config, sparkConf, args)
  }
}
