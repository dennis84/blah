package blah.user

import java.time.ZonedDateTime
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object UserAlgo {
  def train(rdd: RDD[String], ctx: SparkSession, args: Array[String]) = {
    import ctx.implicits._
    val reader = ctx.read.schema(UserSchema())
    reader.json(rdd).createOrReplaceTempView("user")
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
               |FROM user""".stripMargin)
      .filter("user is not null")
      .as[UserEvent]
      .groupByKey(_.user)
      .mapGroups { case(user, events) =>
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
        User(
          id = user,
          user = user,
          date = event.date.toString,
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