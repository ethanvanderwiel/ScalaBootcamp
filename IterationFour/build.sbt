name := "iterationFour"
val http4sVersion = "0.18.23"
libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.6" % Test
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.json4s" %% "json4s-native" % "3.6.7"
)
libraryDependencies ++= Seq(
  "jsonz" %% "jsonz" % "1.5.0"
)
libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe" % http4sVersion,
 
  "io.circe" %% "circe-generic" % "0.9.3",

  "io.circe" %% "circe-literal" % "0.9.3"
)
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
resolvers += "bintray-banno-oss-releases" at "http://dl.bintray.com/banno/oss"
val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.6.7"
version := "1.0"


scalaVersion := "2.11.0"