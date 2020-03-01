name := "TheJoyOfScala"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.30"

libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
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

libraryDependencies ++= monix
