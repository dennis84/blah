scalaVersion in ThisBuild := "2.11.8"

lazy val commonSettings = Seq(
  organization  := "com.github.dennis84",
  version       := "0.1.0",
  scalaVersion  := "2.11.8",
  scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")
)

lazy val root =
  (project in file("."))
    .aggregate(
      `blah-testkit`,
      `blah-http`,
      `blah-json`,
      `blah-elastic`,
      `blah-api`,
      `blah-serving`,
      `blah-algo`
    )

lazy val `blah-testkit` = project
  .settings(commonSettings: _*)

lazy val `blah-http` = project
  .settings(commonSettings: _*)

lazy val `blah-json` = project
  .settings(commonSettings: _*)
  .dependsOn(`blah-testkit` % "test->test")

lazy val `blah-elastic` = project
  .settings(commonSettings: _*)
  .dependsOn(`blah-testkit` % "test->test")
  .dependsOn(`blah-json`)
  .dependsOn(`blah-http`)

lazy val `blah-api` = project
  .settings(commonSettings: _*)
  .dependsOn(`blah-json`)
  .dependsOn(`blah-http`)

lazy val `blah-serving` = project
  .settings(commonSettings: _*)
  .dependsOn(`blah-json`)
  .dependsOn(`blah-http`)
  .dependsOn(`blah-elastic`)

lazy val `blah-algo` = project
  .settings(commonSettings: _*)
  .dependsOn(`blah-testkit` % "test->test")
  .dependsOn(`blah-json`)
  .dependsOn(`blah-elastic`)

lazy val `blah-tests` = project
  .settings(commonSettings: _*)
  .dependsOn(`blah-testkit` % "test->test")
  .dependsOn(`blah-http`)
