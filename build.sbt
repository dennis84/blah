import com.typesafe.sbt.packager.docker._

val res = Seq(
  "clojars" at "https://clojars.org/repo/"
)

val deps = Seq(
  "org.scalatest"           % "scalatest_2.11"                         % "2.2.4" % "test",
  "com.typesafe.akka"      %% "akka-actor"                             % "2.3.12",
  "com.typesafe.akka"       % "akka-stream-experimental_2.11"          % "1.0",
  "com.typesafe.akka"       % "akka-http-core-experimental_2.11"       % "1.0",
  "com.typesafe.akka"       % "akka-http-experimental_2.11"            % "1.0",
  "com.typesafe.akka"       % "akka-http-spray-json-experimental_2.11" % "1.0",
  "org.apache.kafka"       %% "kafka"                                  % "0.8.2.1",
  "org.apache.spark"       %% "spark-core"                             % "1.4.1" % "provided",
  "org.apache.spark"       %% "spark-streaming"                        % "1.4.1",
  "org.apache.spark"       %% "spark-streaming-kafka"                  % "1.4.1",
  "org.apache.spark"       %% "spark-mllib"                            % "1.4.1",
  "org.elasticsearch"      %% "elasticsearch-spark"                    % "2.1.1",
  "com.github.nscala-time" %% "nscala-time"                            % "2.0.0",
  "com.softwaremill"       %% "reactive-kafka"                         % "0.7.0",
  "org.clojars.timewarrior" % "ua-parser"                              % "1.3.0",
  "net.virtual-void"       %% "json-lenses"                            % "0.6.1"
)

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.6",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Ywarn-unused-import")
)

lazy val root = (project in file("."))
  .aggregate(core, api, serving)

lazy val core = (project in file("blah-core"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= deps,
    resolvers ++= res)

lazy val algo = (project in file("blah-algo"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
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
      case PathList(ps @ _*) if ps.last endsWith ".css"  => MergeStrategy.first
      case x =>
        val oldStrategy = (assemblyMergeStrategy in assembly).value
        oldStrategy(x)
    }
  ).settings(
    packageName in Docker := "blah/spark-submit",
    version in Docker := version.value,
    dockerBaseImage := "blah/spark-base",
    dockerEntrypoint := Seq(
      "spark-submit",
      "--class", "blah.algo.Submit",
      "--master", "spark://sparkmaster:7077",
      "/opt/docker/bin/algo-assembly-0.1.0.jar")
  )
  .dependsOn(core)

lazy val api = (project in file("blah-api"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
  .settings(
    packageName in Docker := "blah/api",
    version in Docker := version.value,
    dockerBaseImage := "java:7",
    dockerExposedPorts := Seq(8000)
  )
  .dependsOn(core)

lazy val serving = (project in file("blah-serving"))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(commonSettings: _*)
  .settings(
    packageName in Docker := "blah/serving",
    version in Docker := version.value,
    dockerBaseImage := "java:7",
    dockerExposedPorts := Seq(8001)
  )
  .dependsOn(core % "compile->compile;test->test")
