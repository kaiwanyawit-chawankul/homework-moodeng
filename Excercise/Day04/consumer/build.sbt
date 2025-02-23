name := "Scala Long running Example"

version := "0.1"

scalaVersion := "2.13.10"

// Compiler options (optional, but recommended)
scalacOptions := Seq(
  "-encoding", "UTF-8", // Ensures consistent encoding
  "-deprecation", // Warn about deprecated features
  "-feature", // Warn about features that need explicit import
  "-unchecked", // Warn about unchecked operations
  "-Xlint", // Enable recommended additional linting checks
  "-Ywarn-dead-code" // Warn about dead code
)

// Add resolver for Sonatype repository, where ScalaPB is hosted
resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.mavenCentral

libraryDependencies ++= Seq( // Use ++= for adding multiple dependencies
  "org.apache.kafka" % "kafka-clients" % "3.9.0", // Use a recent version
  "org.slf4j" % "slf4j-api" % "2.0.16", // SLF4j API (interface)
  "ch.qos.logback" % "logback-classic" % "1.5.16", // Logback implementation (choose one)
  "com.datastax.oss" % "java-driver-core" % "4.17.0", // Cassandra driver
  "com.datastax.oss" % "java-driver-query-builder" % "4.17.0",
  "com.google.protobuf" % "protobuf-java" % "4.29.3", // Use a recent version
  "com.google.protobuf" % "protobuf-java-util" % "4.29.3",
  "com.thesamet.scalapb" %%% "scalapb-runtime" % "0.11.11",
  "com.thesamet.scalapb" %% "scalapb-json4s" % "0.12.1",  // Add this for JSON (de)serialization
  // other dependencies...
)

// addSbtPlugin("com.thesamuelsson" % "sbt-protobuf" % "0.12.0")

// Optional: Add sbt-assembly plugin if you want to package the app
enablePlugins(AssemblyPlugin)

assemblyJarName in assembly := "app.jar"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) if xs.map(_.toLowerCase).contains("manifest.mf") =>
    MergeStrategy.discard
  case PathList("META-INF", "services", xs @ _*) => MergeStrategy.discard
  case PathList("module-info.class") => MergeStrategy.discard // Add this line
  case x => MergeStrategy.first
}

Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value)
