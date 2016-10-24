import com.typesafe.sbt.packager.docker._

val res = Seq(
  "clojars" at "https://clojars.org/repo/"
)

val deps = Seq(
  "org.scalactic"                  %% "scalactic"                              % "3.0.0" % "test",
  "org.scalamock"                  %% "scalamock-scalatest-support"            % "3.2.2" % "test",
  "com.typesafe.akka"              %% "akka-actor"                             % "2.4.8",
  "com.typesafe.akka"              %% "akka-testkit"                           % "2.4.8",
  "com.typesafe.akka"              %% "akka-stream"                            % "2.4.8",
  "com.typesafe.akka"              %% "akka-stream-testkit"                    % "2.4.8",
  "com.typesafe.akka"              %% "akka-http-core"                         % "2.4.8",
  "com.typesafe.akka"              %% "akka-http-testkit"                      % "2.4.8",
  "com.typesafe.akka"              %% "akka-http-experimental"                 % "2.4.8",
  "com.typesafe.akka"              %% "akka-http-spray-json-experimental"      % "2.4.8",
  "com.typesafe.akka"              %% "akka-stream-kafka"                      % "0.11-M4",
  "com.typesafe.akka"              %% "akka-slf4j"                             % "2.4.8",
  "ch.qos.logback"                  % "logback-classic"                        % "1.1.7",
  "net.logstash.logback"            % "logstash-logback-encoder"               % "4.7",
  "org.apache.spark"               %% "spark-core"                             % "2.0.0" % "provided",
  "org.apache.spark"               %% "spark-streaming"                        % "2.0.0" % "provided",
  "org.apache.spark"               %% "spark-streaming-kafka-0-10"             % "2.0.0",
  "org.apache.spark"               %% "spark-mllib"                            % "2.0.0",
  "org.clojars.timewarrior"         % "ua-parser"                              % "1.3.0",
  "net.virtual-void"               %% "json-lenses"                            % "0.6.1",
  "com.maxmind.geoip2"              % "geoip2"                                 % "2.5.0"
)

scalaVersion in ThisBuild := "2.11.8"

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file("."))
  .aggregate(core, api, serving, algo, elastic)

lazy val testkit = (project in file("blah-testkit"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= deps,
    resolvers ++= res)

lazy val core = (project in file("blah-core"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= deps,
    resolvers ++= res)
  .dependsOn(testkit % "test->test")

lazy val elastic = (project in file("blah-elastic"))
  .settings(commonSettings: _*)
  .dependsOn(core % "compile->compile;test->test")

lazy val api = (project in file("blah-api"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
  .settings(assemblyMergeStrategy in assembly := defaultMergeStrategy)
  .settings(
    packageName in Docker := "blah/api",
    version in Docker := version.value,
    dockerBaseImage := "blah/java",
    dockerExposedPorts := Seq(8000)
  )
  .dependsOn(core % "compile->compile;test->test")

lazy val serving = (project in file("blah-serving"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
  .settings(assemblyMergeStrategy in assembly := defaultMergeStrategy)
  .settings(
    packageName in Docker := "blah/serving",
    version in Docker := version.value,
    dockerBaseImage := "blah/java",
    dockerExposedPorts := Seq(8001)
  )
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(elastic)

lazy val algo = (project in file("blah-algo"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
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
  .dependsOn(core % "compile->compile;test->test")
  .dependsOn(elastic)

val defaultMergeStrategy: String => sbtassembly.MergeStrategy = {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}
