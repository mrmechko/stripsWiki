// Turn this project into a Scala.js project by importing these settings

import sbt.Keys._
import com.lihaoyi.workbench.Plugin._
import spray.revolver.AppProcess
import spray.revolver.RevolverPlugin.Revolver


val wiki = crossProject.settings(
  scalaVersion := "2.11.6",
  version := "0.1-SNAPSHOT",
  resolvers += Resolver.sonatypeRepo("snapshots"),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "upickle" % "0.3.4",
    "com.lihaoyi" %%% "autowire" % "0.2.5",
    "com.lihaoyi" %%% "scalatags" % "0.5.2",
    "com.github.mrmechko" %%% "strips2" % "0.0.1-SNAPSHOT"
  )
).jsSettings(
  workbenchSettings:_*
).jsSettings(
  name := "Client",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.0"
  ),
  bootSnippet := "wiki.ScalaJSwiki().main();"
).jvmSettings(
  Revolver.settings:_*
).jvmSettings(
  name := "Server",
  libraryDependencies ++= Seq(
    "io.spray" %% "spray-can" % "1.3.1",
    "io.spray" %% "spray-routing" % "1.3.1",
    "com.typesafe.akka" %% "akka-actor" % "2.3.2",
    "org.webjars" % "bootstrap" % "3.2.0"
  )
)

val wikiJS = wiki.js
val wikiJVM = wiki.jvm.settings(
  (resources in Compile) += {
    (fastOptJS in (wikiJS, Compile)).value
    (artifactPath in (wikiJS, Compile, fastOptJS)).value
  }
)
