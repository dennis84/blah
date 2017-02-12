scalaVersion in ThisBuild := "2.11.8"

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file("."))
  .aggregate(`blah-api`, `blah-serving`)

lazy val `blah-api` = project
  .settings(commonSettings: _*)

lazy val `blah-serving` = project
  .settings(commonSettings: _*)
