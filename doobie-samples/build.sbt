scalaVersion := "2.13.0" // Scala 2.12/13

//scalacOptions += "-Ypartial-unification" // 2.11.9+

lazy val doobieVersion = "0.8.8"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2" % doobieVersion,
  "org.tpolecat" %% "doobie-h2" % doobieVersion,
  "org.tpolecat" %% "doobie-refined" % doobieVersion,
  "eu.timepit" %% "refined" % "0.9.12",
  "eu.timepit" %% "refined-cats" % "0.9.12", // optional
)
