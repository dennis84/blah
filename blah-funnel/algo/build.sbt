enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

organization := "com.github.dennis84"

version := "0.1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq(
  "-unchecked",
  "-feature",
  "-deprecation",
  "-encoding",
  "utf8"
)

resolvers ++= Seq(
  "clojars" at "https://clojars.org/repo/"
)

libraryDependencies ++= Seq(
  "com.typesafe"            %  "config"                     % "1.3.1",
  "org.apache.spark"        %% "spark-core"                 % "2.0.0" % "provided",
  "org.apache.spark"        %% "spark-streaming"            % "2.0.0" % "provided",
  "org.apache.spark"        %% "spark-streaming-kafka-0-10" % "2.0.0",
  "org.apache.spark"        %% "spark-mllib"                % "2.0.0",
  "com.typesafe.akka"       %% "akka-actor"                 % "2.4.8",
  "io.spray"                %% "spray-json"                 % "1.3.2",
  "net.logstash.log4j"      %  "jsonevent-layout"           % "1.7",
  "org.scalatest"           %% "scalatest"                  % "3.0.0" % "test"
)

parallelExecution in Test := false

target in assembly := file("target/docker/stage/opt/docker/bin/")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}

packageName in Docker := "blah/funnel-algo"

version in Docker := version.value

dockerBaseImage := "blah/spark-mesos"

dockerEntrypoint := Seq(
  "/opt/spark/bin/spark-submit",
  "--class", "blah.funnel.Main",
  "--conf", "spark.mesos.executor.docker.image=blah/spark-mesos",
  "/opt/docker/bin/algo-assembly-0.1.0.jar"
)
