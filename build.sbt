name := "speechtotexttrial"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies += "com.google.cloud" % "google-cloud-speech" % "1.22.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test

unmanagedJars in Compile += file("lib/subtitle-0.9.2-SNAPSHOT-jar-with-dependencies.jar")