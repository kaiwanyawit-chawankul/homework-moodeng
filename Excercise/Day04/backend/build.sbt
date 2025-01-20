name := "MouseTracker"
version := "0.1"
scalaVersion := "2.13.8"

val akkaHttpVersion = "10.2.10" // Use a more recent 10.2.x version
val akkaVersion = "2.6.20" // Align Akka Stream and Akka HTTP versions

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion, // Use Spray JSON for marshalling
  "com.typesafe.akka" %% "akka-stream-kafka" % "3.0.0",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "io.circe" %% "circe-core" % "0.14.1"
)

resolvers += "Akka library repository".at("https://repo.akka.io/maven")
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