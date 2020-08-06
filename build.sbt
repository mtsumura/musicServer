name := """musicServer"""
organization := "tsumura"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test

//memcached changes
libraryDependencies += "com.github.mumoshu" %% "play2-memcached-play26" % "0.9.2"
libraryDependencies += cacheApi
resolvers += "Spy Repository" at "http://files.couchbase.com/maven2" // required to resolve `spymemcached`, the plugin's dependency.


//jdbc changes
libraryDependencies += jdbc
libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.19"
)

//aws java sdk
libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.11.815"
)



// Adds additional packages into Twirl
//TwirlKeys.templateImports += "tsumura.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "tsumura.binders._"
