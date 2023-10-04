ThisBuild / version := "0.0.1-SNAPSHOT"


lazy val versions = new {
  val scalaTest = "3.2.15"
  val scalaVersion = "2.13.12"
  val akkaVersion = "2.6.1"
  val akkaHttpVersion = "10.2.2"

}

ThisBuild / scalaVersion := versions.scalaVersion

val commonSettings = Seq (
libraryDependencies ++= {
  Seq (
    "su.litvak.chromecast"                        % "api-v2"                            % "0.11.3",
    // This here is bumped down since there are errors if they are too new. The `java.lang.VerifyError` error will be there.
    // Might be avoided if we build the chromecast jar with the same javac as the other packages.
    "com.typesafe.akka"                           %% "akka-http"                        % versions.akkaHttpVersion,
    "com.typesafe.akka"                           %% "akka-stream"                      % versions.akkaVersion,
    "com.typesafe.akka"                           %% "akka-actor"                       % versions.akkaVersion,
    "com.typesafe.scala-logging"                  %% "scala-logging"                    % "3.9.2",
    "ch.qos.logback"                              % "logback-classic"                   % "1.2.3",
    "com.github.pureconfig"                       %% "pureconfig"                       % "0.11.1",
    "org.apache.tika"                             % "tika-parsers"                      % "1.21",
    "org.scalatest"                               %% "scalatest"                        % versions.scalaTest          % "test",
  )},
  scalacOptions ++= Seq("-Ymacro-annotations", "-Xfatal-warnings", "-deprecation", "-unchecked", "-encoding", "utf8"),
)

lazy val backend = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    commonSettings,
    name := "caster",
    organization := "com.funny",
    Compile / run / mainClass := Some(s"${organization.value}.MediaServer"),
  )