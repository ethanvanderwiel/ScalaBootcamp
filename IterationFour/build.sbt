name := "iterationFour"
val http4sVersion = "0.18.23"
libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.6" % Test
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

version := "1.0"


scalaVersion := "2.12.0"