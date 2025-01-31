name := "Heatmap"
version := "0.1"
scalaVersion := "2.13.8"

val akkaHttpVersion = "10.5.3" // Use a more recent 10.2.x version
val akkaVersion = "2.8.8" // Align Akka Stream and Akka HTTP versions

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion, // Use Spray JSON for marshalling
  "com.typesafe.akka" %% "akka-stream-kafka" % "4.0.2",
  "com.google.protobuf" % "protobuf-java" % "4.29.3", // Use a recent version
  "com.google.protobuf" % "protobuf-java-util" % "4.29.3",
  "com.thesamet.scalapb" %%% "scalapb-runtime" % "0.11.11",
  "com.thesamet.scalapb" %% "scalapb-json4s" % "0.12.1",  // Add this for JSON (de)serialization
  "com.datastax.oss" % "java-driver-core" % "4.17.0",
  "io.spray" %% "spray-json" % "1.3.6",
  "com.typesafe.play" %% "play-json" % "2.10.6"
  // other dependencies...
)

resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.mavenCentral

enablePlugins(AssemblyPlugin)

mainClass in (Compile, run) := Some("com.example.Main")
assembly / mainClass := Some("com.example.Main")
assembly / assemblyJarName := "app.jar"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case "application.conf" => MergeStrategy.concat
  case "version.conf" => MergeStrategy.concat // Explicitly concat version.conf
  case x if x.endsWith(".properties") => MergeStrategy.discard
  case x if x.endsWith(".MF") => MergeStrategy.discard
  case x if x.endsWith(".class") => MergeStrategy.last
  case x if x.endsWith(".proto") => MergeStrategy.last
  case x if x.endsWith(".json") => MergeStrategy.last
  case x if x.endsWith(".txt") => MergeStrategy.last
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value)
