package blah.similarity

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
    val eventsDf = spark.sql("SELECT collection, props as p FROM events")
      .withColumn("props", from_json($"p", SimilarityPropsSchema()))
      .drop("p")
      .toDF

    val df = spark.createDataFrame(eventsDf.rdd, SimilaritySchema())
    df.createOrReplaceTempView("events")
    
    val output = SimilarityAlgo.train(spark, args shift "path")
    output.writeToElastic("similarity", "similarity")

    val props = new Properties
    props.put("bootstrap.servers", config.getString("producer.broker.list"))
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    output.toJSON.writeToKafka(props, x =>
      new ProducerRecord[String, String]("trainings", s"similarity@$x"))

    spark.stop
  }
}
