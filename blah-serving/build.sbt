enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

libraryDependencies ++= Seq(
  Dependencies.scalatest % "test",
  Dependencies.scalactic % "test",
  Dependencies.scalamock % "test",
  Dependencies.akkaSlf4j,
  Dependencies.akkaHttpCore,
  Dependencies.akkaHttp,
  Dependencies.akkaHttpTestkit % "test",
  Dependencies.akkaHttpSprayJson,
  Dependencies.akkaStreamKafka,
  Dependencies.logbackClassic,
  Dependencies.logstashLogbackEncoder
)

assemblyMergeStrategy in assembly := Assembly.defaultMergeStrategy

packageName in Docker := "blah/serving"

version in Docker := version.value

dockerBaseImage := "blah/java"

dockerExposedPorts := Seq(8001)
