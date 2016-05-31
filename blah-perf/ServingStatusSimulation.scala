import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ServingStatusSimulation extends Simulation {
  val httpConf = http
    .baseURL("http://serving.marathon.mesos:8001")
    .header("Content-Type", "application/json")

  val scn = scenario("BasicSimulation")
    .repeat(100) {
      exec(http("request_1").get("/"))
    }

  setUp(
    scn.inject(rampUsers(2000) over (10 seconds))
  ).protocols(httpConf)
}
