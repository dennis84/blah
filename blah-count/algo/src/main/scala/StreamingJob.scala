package blah.count

import java.util.Properties
import scala.concurrent.ExecutionContext
import org.apache.spark.SparkConf
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
      """|ctx._source.count += params.count;
         |ctx._source.price += params.price
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
      val spark = SparkSessionSingleton
        .getInstance(rdd.sparkContext.getConf)

      val reader = spark.read.schema(CountSchema())
      reader.json(rdd).createOrReplaceTempView("events")

      val output = CountAlgo.train(spark, args)
      output.writeToElastic("count", "count")

      val props = new Properties
      props.put("bootstrap.servers", config.getString("producer.broker.list"))
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

      output.toJSON.writeToKafka(props, x =>
        new ProducerRecord[String, String]("trainings", s"count@$x"))
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
