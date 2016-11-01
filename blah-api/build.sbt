enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

libraryDependencies ++= Seq(
  Dependencies.scalatest % "test",
  Dependencies.scalactic % "test",
  Dependencies.akkaStreamTestkit % "test",
  Dependencies.akkaStreamKafka,
  Dependencies.akkaSlf4j,
  Dependencies.akkaHttpCore,
  Dependencies.akkaHttp,
  Dependencies.akkaHttpSprayJson,
  Dependencies.logbackClassic,
  Dependencies.logstashLogbackEncoder,
  Dependencies.sparkCore
)

assemblyMergeStrategy in assembly := Assembly.defaultMergeStrategy

packageName in Docker := "blah/api"

version in Docker := version.value

dockerBaseImage := "blah/java"

dockerExposedPorts := Seq(8000)
