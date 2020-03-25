name := "TheJoyOfScala"

version := "0.1"

scalaVersion := "2.12.10"

val tsecV = "0.0.1-M11"
libraryDependencies ++= Seq(
  "io.github.jmcardon" %% "tsec-hash-jca" % tsecV,
//  "io.github.jmcardon" %% "tsec-hash-bouncy" % tsecV,
//  "com.roundeights" %% "hasher" % "1.2.0",
)

