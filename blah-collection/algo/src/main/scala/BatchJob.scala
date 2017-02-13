package blah.collection

import java.util.Properties
import scala.concurrent.ExecutionContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.kafka.clients.producer.ProducerRecord
import com.typesafe.config.Config
import DatasetKafkaWriter._
import DatasetElasticWriter._
import FindOpt._

object BatchJob {
  def run(
    config: Config,
    sparkConf: SparkConf,
    args: Array[String]
  )(implicit ec: ExecutionContext) {
    val spark = SparkSession.builder.config(sparkConf).getOrCreate()

    val jdbcDf = spark.read
      .format("jdbc")
      .option("url", config.getString("postgres.url"))
      .option("dbtable", "events")
      .option("user", config.getString("postgres.user"))
      .option("password", config.getString("postgres.password"))
      .load()

    jdbcDf.createOrReplaceTempView("events")
    
    import spark.implicits._
    val eventsDf = spark.sql("SELECT date, collection FROM events").toDF

    val df = spark.createDataFrame(eventsDf.rdd, CollectionCountSchema())
    df.createOrReplaceTempView("events")

    val output = CollectionCountAlgo.train(spark, args)
    output.writeToElastic("collection", "count")

    val props = new Properties
    props.put("bootstrap.servers", config.getString("producer.broker.list"))
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    output.toJSON.writeToKafka(props, x =>
      new ProducerRecord[String, String]("trainings", s"collection@$x"))

    spark.stop
  }
}
