name := "http4s-trial"

version := "0.1"

scalaVersion := "2.12.8"

val http4sVer = "0.20.1"
val tapirVer  = "0.8.3"

lazy val tapir_trial = project.in(file("tapir_trial"))
  .settings(name := "tapir trial")
  .settings(
    scalacOptions ++= Seq(
      "-encoding", "utf8", // Option and arguments on same line
      "-Xfatal-warnings",  // New lines for each options
      "-Ypartial-unification",
      "-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    ),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "1.3.1",
      "org.http4s"             %% "http4s-core"         % "0.20.1",
      "org.http4s"             %% "http4s-dsl"          % http4sVer,
      "org.http4s"             %% "http4s-blaze-server" % http4sVer,
      "com.softwaremill.tapir" %% "tapir-core"          % tapirVer,
      "com.softwaremill.tapir" %% "tapir-http4s-server" % tapirVer,
      "org.slf4j"              %  "slf4j-simple"        % "1.6.4",
    )
  )
