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

libraryDependencies ++= Seq( // Use ++= for adding multiple dependencies
  "org.apache.kafka" % "kafka-clients" % "3.6.1", // Use a recent version
  "org.slf4j" % "slf4j-api" % "2.0.9", // SLF4j API (interface)
  "ch.qos.logback" % "logback-classic" % "1.4.11" // Logback implementation (choose one)
  // other dependencies...
)

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