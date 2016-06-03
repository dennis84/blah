import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ServingCountSimulation extends Simulation {
  val httpConf = http
    .baseURL("http://serving.blah.local")
    .header("Content-Type", "application/json")

  val all = http("all")
    .post("/count")
    .body(StringBody("""{"collection": "pageviews"}""")).asJSON

  val scn = scenario("BasicSimulation")
    .repeat(100) {
      exec(all)
    }

  setUp(
    scn.inject(rampUsers(500) over (10 seconds))
  ).protocols(httpConf)
}
