package blah.funnel

import java.util.Properties
import scala.concurrent.ExecutionContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
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
    val eventsDf = spark.sql("SELECT date, props as p FROM events")
      .withColumn("props", from_json($"p", FunnelPropsSchema()))
      .drop("p")
      .toDF

    val df = spark.createDataFrame(eventsDf.rdd, FunnelSchema())
    df.createOrReplaceTempView("events")

    val output = DirectPathAlgo.train(spark, args shift "path")
    output.writeToElastic("funnel", "funnel")

    val props = new Properties
    props.put("bootstrap.servers", config.getString("producer.broker.list"))
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    output.toJSON.writeToKafka(props, x =>
      new ProducerRecord[String, String]("trainings", s"funnel@$x"))

    spark.stop
  }
}
