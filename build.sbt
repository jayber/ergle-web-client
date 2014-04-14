import com.typesafe.sbt.SbtNativePackager.packageArchetype

name := "ergle-web-client"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.springframework" % "spring-context" % "3.2.2.RELEASE",
  "javax.inject" % "javax.inject" % "1",
  "org.mockito" % "mockito-core" % "1.9.5",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
  "org.specs2" %% "specs2" % "2.3.7" % "test",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "javax.mail" % "mail" % "1.4.7"
)

play.Project.playScalaSettings

packageArchetype.java_application

