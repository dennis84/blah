import com.typesafe.sbt.packager.docker._
import Dependencies._

scalaVersion in ThisBuild := "2.11.8"

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file("."))
  .aggregate(json, api, serving, algo, elastic)

lazy val testkit = (project in file("blah-testkit"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= scalatest +: `spark-streaming-mlib`)

lazy val http = (project in file("blah-http"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= `akka-http`)

lazy val json = (project in file("blah-json"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= Seq(`spray-json`))
  .dependsOn(testkit % "test->test")

lazy val elastic = (project in file("blah-elastic"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= Seq(`json-lenses`))
  .dependsOn(testkit % "test->test")
  .dependsOn(json)
  .dependsOn(http)

lazy val api = (project in file("blah-api"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++=
    `akka-actor` ++
    `akka-stream` ++
    `akka-stream-kafka` ++
    `logback-logstash` ++
    `akka-http` ++ Seq(
      scalatest % "test",
      scalactic % "test",
      "org.apache.spark" %% "spark-core" % "2.0.0"
    ))
  .settings(assemblyMergeStrategy in assembly := defaultMergeStrategy)
  .settings(
    packageName in Docker := "blah/api",
    version in Docker := version.value,
    dockerBaseImage := "blah/java",
    dockerExposedPorts := Seq(8000)
  )
  .dependsOn(json)
  .dependsOn(http)

lazy val serving = (project in file("blah-serving"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++=
    `akka-actor` ++
    `akka-stream` ++
    `akka-stream-kafka` ++
    `akka-http` ++
    `logback-logstash` ++ Seq(
      scalatest % "test",
      scalactic % "test",
      scalamock % "test"
    ))
  .settings(assemblyMergeStrategy in assembly := defaultMergeStrategy)
  .settings(
    packageName in Docker := "blah/serving",
    version in Docker := version.value,
    dockerBaseImage := "blah/java",
    dockerExposedPorts := Seq(8001)
  )
  .dependsOn(json)
  .dependsOn(http)
  .dependsOn(elastic)

lazy val algo = (project in file("blah-algo"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++=
    `spark-streaming-mlib` ++ Seq(
      scalatest % "test",
      scalactic % "test",
      geoip2,
      `ua-parser`,
      `logstash-jsonevent`
    ))
  .settings(resolvers ++= Seq("clojars" at "https://clojars.org/repo/"))
  .settings(parallelExecution in Test := false)
  .settings(
    target in assembly := file("blah-algo/target/docker/stage/opt/docker/bin/"),
    assemblyMergeStrategy in assembly := defaultMergeStrategy)
  .settings(
    packageName in Docker := "blah/algo",
    version in Docker := version.value,
    dockerBaseImage := "blah/spark-mesos",
    dockerEntrypoint := Seq(
      "/opt/spark/bin/spark-submit",
      "--class", "blah.algo.Submit",
      "--conf", "spark.mesos.executor.docker.image=blah/spark-mesos",
      "/opt/docker/bin/algo-assembly-0.1.0.jar")
  )
  .dependsOn(testkit % "test->test")
  .dependsOn(json)
  .dependsOn(elastic)

val defaultMergeStrategy: String => sbtassembly.MergeStrategy = {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}
