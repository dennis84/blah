import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ApiEventSimulation extends Simulation {
  val httpConf = http
    .baseURL("http://api.blah.local")
    .header("Content-Type", "application/json")

  val insert = http("all")
    .post("/events/view")
    .body(StringBody("""|{
                        |  "item": "item-1",
                        |  "title": "Visit item-1",
                        |  "user": "dennis",
                        |  "email": "d.dietrich84+blah-1@gmail.com",
                        |  "ip": "8.8.8.8",
                        |  "userAgent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/45.0.2454.85 Chrome/45.0.2454.85 Safari/537.36"
                        |}""".stripMargin)).asJSON

  val scn = scenario("BasicSimulation")
    .repeat(10) {
      exec(insert)
    }

  setUp(
    scn.inject(rampUsers(2000) over (10 seconds))
  ).protocols(httpConf)
}
