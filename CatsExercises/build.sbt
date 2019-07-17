name := "catsExercises"


version := "1.0"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.0.0"
scalacOptions ++= Seq(
        "-Ypartial-unification",
        "-Xfatal-warnings"
    )


scalaVersion := "2.12.8"