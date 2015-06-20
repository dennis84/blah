enablePlugins(JavaAppPackaging)

val deps = Seq(
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

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.6",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file("."))
  .aggregate(core, app)

lazy val core = (project in file("blah-core"))
  .settings(commonSettings: _*)
  .settings(
    name := "core",
    libraryDependencies ++= deps
  )

lazy val app = (project in file("blah-app"))
  .settings(commonSettings: _*)
  .settings(Revolver.settings: _*)
  .settings(
    name := "app"
  ).dependsOn(core)
