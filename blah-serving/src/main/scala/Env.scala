package blah.serving

import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import akka.kafka._
import akka.kafka.scaladsl._
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._
import blah.core.HttpClient
import blah.elastic.{ElasticClient, ElasticUri, MappingUpdater}
import blah.core.JsonDsl._

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
          ("collection" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("item" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
          ("browserFamily" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("browserMajor" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("osFamily" -> ("type" -> "string") ~ ("analyzer" -> "lowercase_keyword")) ~
          ("osMajor" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("deviceFamily" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("platform" -> ("type" -> "string") ~ ("analyzer" -> "lowercase_keyword")) ~
          ("isMobile" -> ("type" -> "boolean")) ~
          ("isTablet" -> ("type" -> "boolean")) ~
          ("isMobileDevice" -> ("type" -> "boolean")) ~
          ("isComputer" -> ("type" -> "boolean")) ~
          ("count" -> ("type" -> "integer")))) ~
      ("recommendation" ->
        ("properties" ->
          ("user" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("collection" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("items" ->
            ("type" -> "nested") ~
            ("properties" ->
              ("item" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
              ("score" -> ("type" -> "double")))))) ~
      ("similarity" ->
        ("properties" ->
          ("item" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("collection" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("similarities" ->
            ("type" -> "nested") ~
            ("properties" ->
              ("item" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
              ("score" -> ("type" -> "double")))))) ~
      ("user" ->
        ("properties" ->
          ("user" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("email" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("firstname" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("lastname" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("ip" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("lng" -> ("type" -> "double")) ~
          ("lat" -> ("type" -> "double")) ~
          ("country" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("countryCode" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("city" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("zipCode" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
          ("events" ->
            ("type" -> "nested") ~
            ("properties" ->
              ("collection" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
              ("item" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
              ("title" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
              ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
              ("ip" -> ("type" -> "string") ~ ("index" -> "not_analyzed"))
            )) ~
          ("nbEvents" -> ("type" -> "integer")))) ~
      ("funnel" ->
        ("properties" ->
          ("name" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("item" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("parent" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("count" -> ("type" -> "integer")))) ~
      ("collection_count" ->
        ("_ttl" -> ("enabled" -> true) ~ ("default" -> "1m")) ~
        ("properties" ->
          ("name" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
          ("count" -> ("type" -> "integer")))) ~
      ("referrer" ->
        ("properties" ->
          ("referrer" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("count" -> ("type" -> "integer")))))
}
