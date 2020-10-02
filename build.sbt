name := "musicServer"
organization := "tsumura"

version := "1.0-SNAPSHOT"

//Testing out Dependency Inversion Principle using sbt and guice.
//idea is that the root web app knows nothing about the implementation of how music is persisted.
//it just depends on the IStorage interface.
//The trick is that it does need to depend on a tiny storageAssembler to bootstrap the persistence implmentation at runtime.
//Unfortunately I had to put all the runtime dependency logic in the root sbt file because of the way the build.sbt are merged into a logical build file.
//TODO: Maybe figure this sbt mess out later.
lazy val IStorage = (project in file("./IStorage"))
lazy val musicStorage = (project in file("musicStorage")).aggregate(IStorage).dependsOn(IStorage)
lazy val storageAssembler = (project in file("storageAssembler")).aggregate(IStorage, musicStorage).dependsOn(IStorage, musicStorage)
lazy val root = (project in file(".")).enablePlugins(PlayScala).aggregate(IStorage, storageAssembler).dependsOn(IStorage, storageAssembler)

scalaVersion := "2.12.3"

libraryDependencies += guice

libraryDependencies += "org.scalamock" %% "scalamock" % "4.4.0" % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test
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

//kafka producer
libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.6.0"
)

//kafka streams
libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "2.6.0",
)
