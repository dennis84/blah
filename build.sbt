val deps = Seq(
  "com.typesafe.akka"      %% "akka-actor"                             % "2.3.12",
  "com.typesafe.akka"      %  "akka-stream-experimental_2.11"          % "1.0",
  "com.typesafe.akka"      %  "akka-http-core-experimental_2.11"       % "1.0",
  "com.typesafe.akka"      %  "akka-http-experimental_2.11"            % "1.0",
  "com.typesafe.akka"      %  "akka-http-spray-json-experimental_2.11" % "1.0",
  "org.apache.kafka"       %% "kafka"                                  % "0.8.2.1",
  "org.apache.spark"       %% "spark-core"                             % "1.4.1",
  "org.apache.spark"       %% "spark-streaming"                        % "1.4.1",
  "org.apache.spark"       %% "spark-streaming-kafka"                  % "1.4.1",
  "org.apache.spark"       %% "spark-mllib"                            % "1.4.1",
  "com.datastax.cassandra" %  "cassandra-driver-core"                  % "2.1.7",
  "com.datastax.spark"     %% "spark-cassandra-connector"              % "1.4.0-M2",
  "com.github.nscala-time" %% "nscala-time"                            % "2.0.0"
)

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.6",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file("."))
  .aggregate(core, api, serving)

lazy val core = (project in file("blah-core"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= deps)

lazy val api = (project in file("blah-api"))
  .settings(commonSettings: _*)
  .dependsOn(core)

lazy val algo = (project in file("blah-algo"))
  .settings(commonSettings: _*)
  .dependsOn(core)

lazy val serving = (project in file("blah-serving"))
  .settings(commonSettings: _*)
  .dependsOn(core)
