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

libraryDependencies ++= Seq(
  "com.typesafe"            %  "config"                     % "1.3.1",
  "org.apache.spark"        %% "spark-core"                 % "2.1.0" % "provided",
  "org.apache.spark"        %% "spark-sql"                  % "2.1.0" % "provided",
  "org.apache.spark"        %% "spark-mllib"                % "2.1.0",
  "org.apache.kafka"        %% "kafka"                      % "0.10.2.0",
  "io.spray"                %% "spray-json"                 % "1.3.2",
  "net.logstash.log4j"      %  "jsonevent-layout"           % "1.7",
  "org.scalatest"           %% "scalatest"                  % "3.0.0" % "test",
  "org.postgresql"          %  "postgresql"                 % "42.0.0"
)

parallelExecution in Test := false

target in assembly := file("target/docker/stage/opt/docker/bin/")
test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}

packageName in Docker := "blah/similarity-algo"

version in Docker := "latest"

dockerBaseImage := "blah/spark-mesos"

dockerEntrypoint := Seq(
  "/opt/spark/bin/spark-submit",
  "--class", "blah.similarity.Main",
  "--conf", "spark.mesos.executor.docker.image=blah/spark-mesos",
  "--driver-class-path", "/opt/postgresql-42.0.0.jar",
  "/opt/docker/bin/algo-assembly-0.1.0.jar"
)
