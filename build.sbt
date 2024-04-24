ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"

lazy val root = (project in file("."))
  .settings(
    name := "netty_in_action_scala"
  )

// https://mvnrepository.com/artifact/io.netty/netty-all
libraryDependencies += "io.netty" % "netty-all" % "4.1.109.Final"
