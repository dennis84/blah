import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ApiStatusSimulation extends Simulation {
  val httpConf = http
    .baseURL("http://api.blah.local")
    .header("Content-Type", "application/json")

  val scn = scenario("BasicSimulation")
    .repeat(100) {
      exec(http("request_1").get("/"))
    }

  setUp(
    scn.inject(rampUsers(2000) over (10 seconds))
  ).protocols(httpConf)
}
