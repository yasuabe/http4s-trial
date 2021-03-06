name := "http4s-trial"

version := "0.1"

scalaVersion := "2.13.1"

val http4sVer     = "0.21.6"
val tapirVer      = "0.16.1"
val circeVer      = "0.13.0"
val catsEffectVer = "2.1.3"

val commonLib = Seq(
  "org.typelevel" %% "cats-effect"         % catsEffectVer,
  "org.http4s"    %% "http4s-core"         % http4sVer,
  "org.http4s"    %% "http4s-dsl"          % http4sVer,
  "org.http4s"    %% "http4s-blaze-server" % http4sVer,
  "org.http4s"    %% "http4s-circe"        % http4sVer,
  "org.slf4j"     %  "slf4j-simple"        % "1.6.4"
)

val circeLib = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVer)

val compileOptions = Seq(
  "-encoding", "utf8", // Option and arguments on same line
  "-Xfatal-warnings",  // New lines for each options
  "-Ypartial-unification",
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)
lazy val tapir_trial = project.in(file("tapir_trial"))
  .settings(name := "tapir trial")
  .settings(
    scalacOptions ++= compileOptions,
    libraryDependencies ++= commonLib ++ Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-core"          % tapirVer,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVer,
    )
  )
lazy val swagger_tapir = project.in(file("swagger_tapir"))
 .settings(name := "swagger tapir")
 .settings(
   scalacOptions ++= compileOptions,
   libraryDependencies ++= commonLib ++ circeLib ++ Seq(
     "com.softwaremill.sttp.tapir" %% "tapir-core"          % tapirVer,
     "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirVer,
     "com.softwaremill.sttp.tapir" %% "tapir-json-circe"    % tapirVer,
     "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"  % tapirVer,
     "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVer,
     "org.webjars"            % "swagger-ui"           % "3.22.2",
     "org.webjars"            % "webjars-locator"      % "0.36"
   )
 )
