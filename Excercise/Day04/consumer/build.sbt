name := "Scala Cron Job Example"

version := "0.1"

scalaVersion := "2.13.10"

// Define the Akka version globally
val akkaVersion = "2.6.20"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.17.3",        // PureConfig for config handling
  "org.slf4j" % "slf4j-api" % "1.7.32",                       // SLF4J logging
  "ch.qos.logback" % "logback-classic" % "1.2.6",              // Logback for logging

  // Akka dependencies (all set to the same version)
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,           // Akka Actor library
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,     // Akka Actor Typed
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,           // SLF4J logging for Akka
  "com.typesafe.akka" %% "akka-http" % "10.2.9",               // Akka HTTP if needed for other routes

  "org.quartz-scheduler" % "quartz" % "2.3.2"                  // Quartz library
)

// Optional: Add sbt-assembly plugin if you want to package the app
enablePlugins(AssemblyPlugin)

assemblyJarName in assembly := "app.jar"