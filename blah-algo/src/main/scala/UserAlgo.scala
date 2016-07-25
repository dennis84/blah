package blah.algo

import java.time.ZonedDateTime
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
        val ord = Ordering[Long].on[String](x =>
          ZonedDateTime.parse(x).toInstant.toEpochMilli).reverse
        val sorted = events.toList.sortBy(_.date)(ord)

        def mergeEvents(xs: List[UserEvent], u: UserEvent): UserEvent = {
          val full = u.firstname.isDefined && u.lastname.isDefined &&
                     u.email.isDefined && u.ip.isDefined
          def getValue[A](a: Option[A], b: Option[A]) =
            if(a.nonEmpty && b.isEmpty) a else b

          if(xs.isEmpty || full) u else {
            val x = xs.head
            mergeEvents(xs.tail, u.copy(
              email = getValue(x.email, u.email),
              firstname = getValue(x.firstname, u.firstname),
              lastname = getValue(x.lastname, u.lastname),
              ip = getValue(x.ip, u.ip)))
          }
        }

        val event = mergeEvents(sorted.tail, sorted.head)
        val geo = event.ip.map(GeoIp.find _).flatten
        val doc = User(
          user = u,
          email = event.email.getOrElse("N/A"),
          firstname = event.firstname.getOrElse(""),
          lastname = event.lastname.getOrElse(""),
          date = event.date.toString,
          lng = geo.map(_.lng).getOrElse(0),
          lat = geo.map(_.lat).getOrElse(0),
          country = geo.map(_.country).flatten.getOrElse("N/A"),
          countryCode = geo.map(_.countryCode).flatten.getOrElse("N/A"),
          city = geo.map(_.city).flatten.getOrElse("N/A"),
          zipCode = geo.map(_.zipCode).flatten.getOrElse("N/A"),
          events = (sorted take 20),
          nbEvents = events.size)
        (u, doc)
      }
  }
}
