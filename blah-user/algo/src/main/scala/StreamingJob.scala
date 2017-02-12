package blah.user

import java.util.Properties
import scala.concurrent.ExecutionContext
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag
import scala.util.{Success, Failure}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.clients.producer.ProducerRecord
import com.typesafe.config.Config
import DatasetKafkaWriter._
import DatasetElasticWriter._

object StreamingJob {
  def run(
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

    val ssc = new StreamingContext(sparkConf,
      Seconds(config.getInt("streaming.batch.interval")))
    val kafkaParams = Map(
      "bootstrap.servers" -> config.getString("consumer.broker.list"),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "trainings")
    val stream = KafkaUtils.createDirectStream[String, String](ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Set("events"), kafkaParams))
      .map(_.value)

    stream.foreachRDD(rdd => if(!rdd.isEmpty) {
      val sparkSession = SparkSessionSingleton
        .getInstance(rdd.sparkContext.getConf)
      val output = UserAlgo.train(rdd, sparkSession, args)

      output.writeToElastic("blah", "user")

      val props = new Properties
      props.put("bootstrap.servers", config.getString("producer.broker.list"))
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

      output.toJSON.writeToKafka(props, x =>
        new ProducerRecord[String, String]("trainings", s"user@$x"))
    })

    stream.print()
    ssc.start()
    ssc.awaitTermination()
  }
}

object SparkSessionSingleton {
  @transient  private var instance: SparkSession = _

  def getInstance(conf: SparkConf): SparkSession = {
    if(instance == null) {
      instance = SparkSession.builder.config(conf).getOrCreate()
    }
    instance
  }
}
