package blah.user

import java.sql.Timestamp
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import org.apache.spark.sql.SparkSession

object UserAlgo {
  def train(ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._
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
               |FROM events""".stripMargin)
      .filter("user is not null")
      .as[UserEvent]
      .groupByKey(_.user)
      .mapGroups { case(user, events) =>
        val ord = Ordering[Long]
          .on[Timestamp](x => x.toInstant.toEpochMilli).reverse
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
        User(
          id = user,
          user = user,
          date = event.date.toInstant
            .atZone(ZoneId.of("UTC"))
            .format(DateTimeFormatter.ISO_INSTANT),
          email = event.email,
          firstname = event.firstname,
          lastname = event.lastname,
          lng = geo.map(_.lng),
          lat = geo.map(_.lat),
          country = geo.map(_.country).flatten,
          countryCode = geo.map(_.countryCode).flatten,
          city = geo.map(_.city).flatten,
          zipCode = geo.map(_.zipCode).flatten,
          events = (sorted take 20),
          nbEvents = sorted.length)
      }
  }
}
