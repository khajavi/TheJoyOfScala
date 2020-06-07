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
lazy val exampleGraalLanguage = (project in file("example-graal-language"))
lazy val pureConfigSamples = project in file("pure-config")
lazy val blog = (project in file("blog"))
lazy val a = project in file("A")
lazy val b = (project in file("B"))

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


lazy val fs2Version = "2.3.0"
lazy val fs2 = Seq(
  "co.fs2" %% "fs2-core" % fs2Version
)
libraryDependencies += "dev.zio" %% "zio" % "1.0.0-RC17"
libraryDependencies += "dev.zio" %% "zio-interop-cats" % "2.0.0.0-RC10"
libraryDependencies += "org.typelevel" %% "cats-mtl-core" % "0.7.0"
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.6"
libraryDependencies ++= fs2

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


libraryDependencies +=
  "org.typelevel" %% "cats-tagless-macros" % "0.11"  //latest version indicated in the badge above

Compile / scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n >= 13 => "-Ymacro-annotations" :: Nil
    case _ => Nil
  }
}

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, n)) if n >= 13 => Nil
    case _ => compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full) :: Nil
  }
}

libraryDependencies += {
  val version = scalaBinaryVersion.value match {
    case "2.10" => "1.0.3"
    case _ ⇒ "2.1.1"
  }
  "com.lihaoyi" % "ammonite" % version % "test" cross CrossVersion.full
}

sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
  Seq(file)
}.taskValue

