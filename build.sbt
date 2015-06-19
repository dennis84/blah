enablePlugins(JavaAppPackaging)

name         := "blah"
organization := "com.github.dennis84"
version      := "0.1"
scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka"      %% "akka-actor"                             % "2.3.11",
  "com.typesafe.akka"      %% "akka-slf4j"                             % "2.3.11",
  "com.typesafe.akka"      %  "akka-stream-experimental_2.11"          % "1.0-RC3",
  "com.typesafe.akka"      %  "akka-http-core-experimental_2.11"       % "1.0-RC3",
  "com.typesafe.akka"      %  "akka-http-experimental_2.11"            % "1.0-RC3",
  "com.typesafe.akka"      %  "akka-http-spray-json-experimental_2.11" % "1.0-RC3",
  "com.datastax.cassandra" %  "cassandra-driver-core"                  % "2.1.6",
  "org.xerial.snappy"      % "snappy-java"                             % "1.1.2-RC3",
  "ch.qos.logback"         %  "logback-classic"                        % "1.1.3"
)

Revolver.settings
