import com.typesafe.sbt.packager.docker._

val res = Seq(
  "clojars" at "https://clojars.org/repo/"
)

val deps = Seq(
  "org.scalatest"                   % "scalatest_2.11"                         % "2.2.6" % "test",
  "com.typesafe.akka"              %% "akka-actor"                             % "2.4.1",
  "com.typesafe.akka"              %% "akka-testkit"                           % "2.4.1",
  "com.typesafe.akka"               % "akka-stream-experimental_2.11"          % "2.0.2",
  "com.typesafe.akka"               % "akka-http-core-experimental_2.11"       % "2.0.2",
  "com.typesafe.akka"               % "akka-http-experimental_2.11"            % "2.0.2",
  "com.typesafe.akka"               % "akka-http-spray-json-experimental_2.11" % "2.0.2",
  "org.apache.spark"               %% "spark-core"                             % "1.6.1" % "provided",
  "org.apache.spark"               %% "spark-streaming"                        % "1.6.1" % "provided",
  "org.apache.spark"               %% "spark-streaming-kafka"                  % "1.6.1",
  "org.apache.spark"               %% "spark-mllib"                            % "1.6.1",
  "org.elasticsearch"              %% "elasticsearch-spark"                    % "2.2.0",
  "com.softwaremill.reactivekafka" %% "reactive-kafka-core"                    % "0.8.5",
  "org.clojars.timewarrior"         % "ua-parser"                              % "1.3.0",
  "net.virtual-void"               %% "json-lenses"                            % "0.6.1",
  "com.maxmind.geoip2"              % "geoip2"                                 % "2.5.0"
)

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.7",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file("."))
  .aggregate(core, api, serving, algo, elastic)

lazy val core = (project in file("blah-core"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= deps,
    resolvers ++= res)

lazy val elastic = (project in file("blah-elastic"))
  .settings(commonSettings: _*)
  .dependsOn(core % "compile->compile;test->test")

lazy val api = (project in file("blah-api"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
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
  .settings(dependencyOverrides ++= Set(
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.6"))
  .settings(
    target in assembly := file("blah-algo/target/docker/stage/opt/docker/bin/"),
    assemblyMergeStrategy in assembly := {
      case PathList("org", "apache", xs @ _*) => MergeStrategy.first
      case PathList("javax", "xml", xs @ _*) => MergeStrategy.first
      case PathList("io", "netty", xs @ _*) => MergeStrategy.first
      case PathList("com", "google", xs @ _*) => MergeStrategy.first
      case PathList("com", "esotericsoftware", xs @ _*) => MergeStrategy.first
      case PathList("com", "codahale", xs @ _*) => MergeStrategy.first
      case PathList("akka", xs @ _*) => MergeStrategy.first
      case "META-INF/io.netty.versions.properties" => MergeStrategy.first
      case "application.conf" => MergeStrategy.first
      case PathList(ps @ _*) if ps.last endsWith ".css"  => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    })
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
