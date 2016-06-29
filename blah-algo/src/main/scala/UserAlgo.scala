package blah.algo

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext

class UserAlgo extends Algo[User] {
  def train(rdd: RDD[String], ctx: SQLContext, args: Array[String]) = {
    import ctx.implicits._
    val reader = ctx.read.schema(UserSchema())
    reader.json(rdd).registerTempTable("event")
    ctx.sql("""|SELECT
               |  date,
               |  collection,
               |  props.user,
               |  props.email,
               |  props.firstname,
               |  props.lastname,
               |  props.item,
               |  props.title,
               |  props.ip
               |FROM event""".stripMargin)
      .filter("user is not null")
      .map(UserEvent(_))
      .groupBy(_.user)
      .collect { case(Some(u), events) =>
        val geo = events.last.ip.map(GeoIp.find _).flatten
        val userEvents = (events takeRight 20).toList.reverse
        val doc = User(
          user = u,
          email = events.last.email.getOrElse("N/A"),
          firstname = events.last.firstname.getOrElse(""),
          lastname = events.last.lastname.getOrElse(""),
          date = events.last.date,
          lng = geo.map(_.lng).getOrElse(0),
          lat = geo.map(_.lat).getOrElse(0),
          country = geo.map(_.country).flatten.getOrElse("N/A"),
          countryCode = geo.map(_.countryCode).flatten.getOrElse("N/A"),
          city = geo.map(_.city).flatten.getOrElse("N/A"),
          zipCode = geo.map(_.zipCode).flatten.getOrElse("N/A"),
          events = userEvents,
          nbEvents = events.size)
        (u, doc)
      }
  }
}
