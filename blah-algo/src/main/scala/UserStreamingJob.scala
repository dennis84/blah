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
      """|ctx._source.events = (ctx._source.events += events).takeRight(20);
         |ctx._source.nbEvents += nbEvents;
         |ctx._source.date = date;
         |if(email) ctx._source.email = email;
         |if(firstname) ctx._source.firstname = firstname;
         |if(lastname) ctx._source.lastname = lastname;
         |if(lng) ctx._source.lng = lng;
         |if(lat) ctx._source.lat = lat;
         |if(country) ctx._source.country = country;
         |if(countryCode) ctx._source.countryCode = countryCode;
         |if(city) ctx._source.city = city;
         |if(zipCode) ctx._source.zipCode = zipCode
         |""".stripMargin.replaceAll("\n", ""))
    sparkConf.set("es.update.script.params",
      """|events:events,
         |nbEvents:nbEvents,
         |email:email,
         |firstname:firstname,
         |lastname:lastname,
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
