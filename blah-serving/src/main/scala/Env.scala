package blah.serving

import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import org.apache.kafka.common.serialization.StringDeserializer
import com.softwaremill.react.kafka.{ReactiveKafka, ConsumerProperties}
import spray.json._
import blah.elastic.{ElasticClient, ElasticUri, MappingUpdater}
import blah.core.JsonDsl._

class Env(implicit system: ActorSystem, mat: Materializer) {
  private val config = system.settings.config
  lazy val websocketRoom = new WebsocketRoom(system)
  lazy val websocketHub = system.actorOf(Props(new WebsocketHub(websocketRoom)))

  lazy val kafka = new ReactiveKafka
  lazy val consumer = kafka.consume(ConsumerProperties(
    bootstrapServers = config.getString("consumer.broker.list"),
    topic = "trainings",
    groupId = "websocket",
    valueDeserializer = new StringDeserializer))

  lazy val elasticClient = new ElasticClient(ElasticUri(
    config.getString("elasticsearch.url")))

  lazy val mappingUpdater = new MappingUpdater(elasticClient)

  lazy val elasticMapping: JsObject =
    ("mappings" ->
      ("count" ->
        ("properties" ->
          ("page" -> ("type" -> "string")) ~
          ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime")) ~
          ("browserFamily" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("browserMajor" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("browserMinor" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("browserPatch" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("osFamily" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("osMajor" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("osMinor" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("osPatch" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("deviceFamily" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("platform" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("isMobile" -> ("type" -> "boolean")) ~
          ("isTablet" -> ("type" -> "boolean")) ~
          ("isMobileDevice" -> ("type" -> "boolean")) ~
          ("isComputer" -> ("type" -> "boolean")) ~
          ("count" -> ("type" -> "integer"))
      )) ~
      ("similarity" ->
        ("properties" ->
          ("user" -> ("type" -> "string")) ~
          ("views" ->
            ("type" -> "nested") ~
            ("properties" ->
              ("page" -> ("type" -> "string")) ~
              ("score" -> ("type" -> "double"))
      )))) ~
      ("user" ->
        ("properties" ->
          ("user" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("ip" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("lng" -> ("type" -> "double")) ~
          ("lat" -> ("type" -> "double")) ~
          ("country" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("countryCode" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("city" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("zipCode" -> ("type" -> "string") ~ ("index" -> "not_analyzed")) ~
          ("date" -> ("type" -> "date") ~ ("format" -> "dateOptionalTime"))
      )))
}
