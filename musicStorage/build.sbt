name := "musicStorage"
organization := "tsumura"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.3"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.25"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test

//aws java sdk
libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.11.815"
)