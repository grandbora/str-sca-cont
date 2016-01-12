name := "str-sca-cont"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "6.31.0",
  "org.specs2" %% "specs2-core" % "3.7" % "test",
  "org.specs2" %% "specs2-mock" % "3.7" % "test"
)
