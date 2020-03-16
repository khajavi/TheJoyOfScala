name := "TheJoyOfScala"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.30"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

val circeVersion = "0.12.3"


lazy val tsecSamples = (project in file("tsec-samples"))
lazy val simulacrum = (project in file("simulacrum"))
lazy val simulacrumExample = (project in file("simulacrum-example")).dependsOn(simulacrum)
lazy val kindProjector = (project in file("kind-projector"))
lazy val doobieSamples = (project in file("doobie-samples"))

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-refined",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)


libraryDependencies ++= Seq(
  "eu.timepit" %% "refined" % "0.9.12",
  "eu.timepit" %% "refined-cats" % "0.9.12", // optional
  //  "eu.timepit" %% "refined-eval"            % "0.9.12", // optional, JVM-only
  //  "eu.timepit" %% "refined-jsonpath"        % "0.9.12", // optional, JVM-only
  "eu.timepit" %% "refined-pureconfig" % "0.9.12", // optional, JVM-only
  //  "eu.timepit" %% "refined-scalacheck"      % "0.9.12", // optional
  //  "eu.timepit" %% "refined-scalaz"          % "0.9.12", // optional
  //  "eu.timepit" %% "refined-scodec"          % "0.9.12", // optional
  //  "eu.timepit" %% "refined-scopt"           % "0.9.12", // optional
  //  "eu.timepit" %% "refined-shapeless"       % "0.9.12"  // optional
)

val monixVersion = "3.1.0"
val monix        = Seq(
  "io.monix" %% "monix",
  "io.monix" %% "monix-eval"
).map(_ % monixVersion)

val refinedVersion = "0.9.10"
val refined        = Seq(
  "eu.timepit" %% "refined",
  "eu.timepit" %% "refined-cats"
).map(_ % refinedVersion)

libraryDependencies ++= monix ++ refined
//scalacOptions += "-Ypartial-unification"
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)


libraryDependencies += "dev.zio" %% "zio" % "1.0.0-RC17"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC10"


scalacOptions ++= Seq(
  "-Ywarn-value-discard",
  "-Xfatal-warnings",
//  "-Xfuture",
//  "-Ypartial-unification",
  "-unchecked",
  "-deprecation",
  "-encoding",
  "UTF-8",
//  "-feature",
//  "-Yliteral-types",
  "-language:existentials",
  "-language:higherKinds"
)

