name := "caster"

organization := "com.funny"

version := "0.0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= {
  val akkaVersion = "2.6.1"
  val akkaHttpVersion = "10.2.2"
  Seq (
    "su.litvak.chromecast"                        % "api-v2"                            % "0.11.2",
    // This here is bumped down since there are errors if they are too new. The `java.lang.VerifyError` error will be there.
    // Might be avoided if we build the chromecast jar with the same javac as the other packages.
    "com.typesafe.akka"                           %% "akka-http"                        % akkaHttpVersion,
    "com.typesafe.akka"                           %% "akka-stream"                      % akkaVersion,
    "com.typesafe.akka"                           %% "akka-actor"                       % akkaVersion,
    "com.typesafe.scala-logging"                  %% "scala-logging"                    % "3.9.2",
    "ch.qos.logback"                              % "logback-classic"                   % "1.2.3",
    "com.github.pureconfig"                       %% "pureconfig"                       % "0.11.1",
    "org.apache.tika"                             % "tika-parsers"                      % "1.21",
  )}