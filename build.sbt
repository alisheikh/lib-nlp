name := "lib-nlp"

organization := "com.gilt"

scalaVersion := "2.11.4"

crossScalaVersions := Seq("2.11.4", "2.10.4")

resolvers += "Typesafe Maven Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"

libraryDependencies ++= Seq(
  "com.gilt" %% "gfc-logging" % "0.0.2",
  "org.apache.opennlp" % "opennlp-tools" % "1.5.3",
  "com.typesafe.play" %% "play-json" % "2.3.6",
  "com.typesafe.play" %% "play-ws" % "2.3.6",
  "edu.mit" % "jwi" % "2.2.3",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.mockito" % "mockito-all" % "1.10.8" % "test"
)

releaseSettings

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

licenses := Seq("MIT License" -> url("https://github.com/gilt/lib-nlp/blob/master/LICENSE"))

homepage := Some(url("https://github.com/gilt/lib-nlp"))

pomExtra := (
  <scm>
    <url>https://github.com/gilt/lib-nlp.git</url>
    <connection>scm:git:git@github.com:gilt/lib-nlp.git</connection>
  </scm>
  <developers>
    <developer>
      <id>cclifford</id>
      <name>Conor Clifford</name>
      <url>https://github.com/conorclifford</url>
    </developer>
  </developers>
)

