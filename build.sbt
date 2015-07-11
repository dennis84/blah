enablePlugins(JavaAppPackaging)

val deps = Seq(
  "com.typesafe.akka"      %% "akka-actor"                             % "2.3.11",
  "com.typesafe.akka"      %  "akka-stream-experimental_2.11"          % "1.0-RC4",
  "com.typesafe.akka"      %  "akka-http-core-experimental_2.11"       % "1.0-RC4",
  "com.typesafe.akka"      %  "akka-http-experimental_2.11"            % "1.0-RC4",
  "com.typesafe.akka"      %  "akka-http-spray-json-experimental_2.11" % "1.0-RC4",
  "org.apache.kafka"       %% "kafka"                                  % "0.8.2.1",
  "org.apache.spark"       %% "spark-core"                             % "1.4.0",
  "org.apache.spark"       %% "spark-streaming"                        % "1.4.0",
  "org.apache.spark"       %% "spark-streaming-kafka"                  % "1.4.0",
  "com.datastax.cassandra" %  "cassandra-driver-core"                  % "2.1.6",
  "com.datastax.spark"     %% "spark-cassandra-connector"              % "1.4.0-M1"
)

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.6",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file("."))
  .aggregate(core, api, admin, example)

lazy val core = (project in file("blah-core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= deps
  )

lazy val api = (project in file("blah-api"))
  .settings(commonSettings: _*)
  .settings(
    name := "api"
  ).dependsOn(core)

lazy val admin = (project in file("blah-admin"))
  .settings(commonSettings: _*)
  .settings(
    name := "admin"
  ).dependsOn(core)

lazy val serving = (project in file("blah-serving"))
  .settings(commonSettings: _*)
  .settings(
    name := "serving"
  ).dependsOn(core, example)

lazy val example = (project in file("blah-example"))
  .settings(commonSettings: _*)
  .settings(
    name := "example"
  ).dependsOn(core)
