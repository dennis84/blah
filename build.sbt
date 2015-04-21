enablePlugins(JavaAppPackaging)

name         := "blah"
organization := "dennis84.github.io"
version      := "0.1"
scalaVersion := "2.11.5"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka"      %% "akka-actor"                        % "2.3.9",
  "com.typesafe.akka"      %% "akka-stream-experimental"          % "1.0-M5",
  "com.typesafe.akka"      %% "akka-http-core-experimental"       % "1.0-M5",
  "com.typesafe.akka"      %% "akka-http-experimental"            % "1.0-M5",
  "com.typesafe.akka"      %% "akka-http-spray-json-experimental" % "1.0-M5"
)

Revolver.settings
