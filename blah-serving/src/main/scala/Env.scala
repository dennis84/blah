package blah.serving

import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import akka.kafka._
import akka.kafka.scaladsl._
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._
import JsonDsl._

class Env(implicit system: ActorSystem, mat: Materializer) {
  private val config = system.settings.config
  lazy val websocketRoom = new WebsocketRoom(system)
  lazy val websocketHub = system.actorOf(Props(new WebsocketHub(websocketRoom)))

  lazy val consumerSettings = ConsumerSettings(system,
    new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(config.getString("consumer.broker.list"))
    .withGroupId("websocket")
  lazy val subscription = Subscriptions.topics("trainings")
  lazy val consumer = Consumer.plainSource(consumerSettings, subscription)

  lazy val elasticClient = new ElasticClient(ElasticUri(
    config.getString("elasticsearch.url")))

  lazy val chronosUrl = config.getString("chronos.url").split(":")
  lazy val chronosClient = new HttpClient(
    chronosUrl(0), chronosUrl(1).toInt)

  lazy val indexUpdater = new MappingUpdater(elasticClient)

  lazy val elasticIndex: JsObject =
    ("settings" ->
      ("analysis" ->
        ("analyzer" ->
          ("lowercase_keyword" ->
            ("type" -> "custom") ~
            ("tokenizer" -> "keyword") ~
            ("filter" -> "lowercase")
    )))) ~
    ("mappings" ->
      ("count" ->
        ("properties" ->
          ("collection" -> ("type" -> "text")) ~
          ("item" -> ("type" -> "text")) ~
          ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
          ("browserFamily" -> ("type" -> "text")) ~
          ("browserMajor" -> ("type" -> "text")) ~
          ("osFamily" -> ("type" -> "text") ~ ("analyzer" -> "lowercase_keyword")) ~
          ("osMajor" -> ("type" -> "text")) ~
          ("deviceFamily" -> ("type" -> "text")) ~
          ("platform" -> ("type" -> "text") ~ ("analyzer" -> "lowercase_keyword")) ~
          ("isMobile" -> ("type" -> "boolean")) ~
          ("isTablet" -> ("type" -> "boolean")) ~
          ("isMobileDevice" -> ("type" -> "boolean")) ~
          ("isComputer" -> ("type" -> "boolean")) ~
          ("count" -> ("type" -> "integer")))) ~
      ("recommendation" ->
        ("properties" ->
          ("user" -> ("type" -> "text")) ~
          ("collection" -> ("type" -> "text")) ~
          ("items" ->
            ("type" -> "nested") ~
            ("properties" ->
              ("item" -> ("type" -> "text")) ~
              ("score" -> ("type" -> "double")))))) ~
      ("similarity" ->
        ("properties" ->
          ("item" -> ("type" -> "text")) ~
          ("collection" -> ("type" -> "text")) ~
          ("similarities" ->
            ("type" -> "nested") ~
            ("properties" ->
              ("item" -> ("type" -> "text")) ~
              ("score" -> ("type" -> "double")))))) ~
      ("user" ->
        ("properties" ->
          ("user" -> ("type" -> "text")) ~
          ("email" -> ("type" -> "text")) ~
          ("firstname" -> ("type" -> "text")) ~
          ("lastname" -> ("type" -> "text")) ~
          ("ip" -> ("type" -> "text")) ~
          ("lng" -> ("type" -> "double")) ~
          ("lat" -> ("type" -> "double")) ~
          ("country" -> ("type" -> "text")) ~
          ("countryCode" -> ("type" -> "text")) ~
          ("city" -> ("type" -> "text")) ~
          ("zipCode" -> ("type" -> "text")) ~
          ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
          ("events" ->
            ("type" -> "nested") ~
            ("properties" ->
              ("collection" -> ("type" -> "text")) ~
              ("item" -> ("type" -> "text")) ~
              ("title" -> ("type" -> "text")) ~
              ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
              ("ip" -> ("type" -> "text"))
            )) ~
          ("nbEvents" -> ("type" -> "integer")))) ~
      ("funnel" ->
        ("properties" ->
          ("name" -> ("type" -> "text")) ~
          ("item" -> ("type" -> "text")) ~
          ("parent" -> ("type" -> "text")) ~
          ("count" -> ("type" -> "integer")))) ~
      ("collection_count" ->
        ("properties" ->
          ("name" -> ("type" -> "text")) ~
          ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
          ("count" -> ("type" -> "integer")))) ~
      ("referrer" ->
        ("properties" ->
          ("referrer" -> ("type" -> "text")) ~
          ("count" -> ("type" -> "integer")))))
}
