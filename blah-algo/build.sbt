enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

libraryDependencies ++= Seq(
  Dependencies.scalatest % "test",
  Dependencies.scalactic % "test",
  Dependencies.sparkCore % "provided",
  Dependencies.sparkStreaming % "provided",
  Dependencies.sparkStreamingKafka,
  Dependencies.sparkMllib,
  Dependencies.geoip2,
  Dependencies.uaParser,
  Dependencies.logstashJsonevent
)

resolvers ++= Seq("clojars" at "https://clojars.org/repo/")

parallelExecution in Test := false

target in assembly := file("blah-algo/target/docker/stage/opt/docker/bin/")

assemblyMergeStrategy in assembly := Assembly.defaultMergeStrategy

packageName in Docker := "blah/algo"

version in Docker := version.value

dockerBaseImage := "blah/spark-mesos"

dockerEntrypoint := Seq(
  "/opt/spark/bin/spark-submit",
  "--class", "blah.algo.Submit",
  "--conf", "spark.mesos.executor.docker.image=blah/spark-mesos",
  "/opt/docker/bin/blah-algo-assembly-0.1.0.jar"
)
