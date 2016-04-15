package blah.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, Row}
import org.apache.spark.sql.types.{StructType,StructField,StringType}

case class User(
  user: String,
  date: String,
  lng: Double,
  lat: Double,
  country: String,
  countryCode: String,
  city: String,
  zipCode: String,
  events: List[UserEvent])

case class UserEvent(
  date: String,
  user: Option[String] = None,
  item: Option[String] = None,
  title: Option[String] = None,
  ip: Option[String] = None)

object UserEvent {
  def apply(r: Row): UserEvent = UserEvent(
    r.getString(0),
    Option(r.getString(1)),
    Option(r.getString(2)),
    Option(r.getString(3)),
    Option(r.getString(4)))
}

object UserSchema {
  def apply() = StructType(Array(
    StructField("date", StringType, true),
    StructField("props", StructType(Array(
      StructField("user", StringType, true),
      StructField("item", StringType, true),
      StructField("title", StringType, true),
      StructField("ip", StringType, true))), true)))
}

class UserAlgo {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    val reader = ctx.read.schema(UserSchema())
    reader.json(rdd).registerTempTable("event")
    ctx.sql("""|SELECT
               |  date,
               |  props.user,
               |  props.item,
               |  props.title,
               |  props.ip
               |FROM event""".stripMargin)
      .map(UserEvent(_))
      .filter(_.user.isDefined)
      .groupBy(_.user)
      .collect { case(Some(u), events) =>
        val geo = events.last.ip.map(GeoIp.find _).flatten
        val userEvents = (events takeRight 20).toList.reverse
        val doc = User(
          user = u,
          date = events.last.date,
          lng = geo.map(_.lng).getOrElse(0),
          lat = geo.map(_.lat).getOrElse(0),
          country = geo.map(_.country).flatten.getOrElse("N/A"),
          countryCode = geo.map(_.countryCode).flatten.getOrElse("N/A"),
          city = geo.map(_.city).flatten.getOrElse("N/A"),
          zipCode = geo.map(_.zipCode).flatten.getOrElse("N/A"),
          events = userEvents)
        (u, doc)
      }
  }
}
