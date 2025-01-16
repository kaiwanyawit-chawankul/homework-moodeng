name := "MouseTracker"
version := "0.1"
scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.2.9",
  "com.typesafe.akka" %% "akka-stream" % "2.6.17", // Akka Stream should be same major version as Akka HTTP
  "de.heikoseeberger" %% "akka-http-circe" % "1.39.2",
  "io.circe"         %% "circe-generic"     % "0.14.1"
)

enablePlugins(AssemblyPlugin)

mainClass in (Compile, run) := Some("com.example.Main")  // Replace with your main class
// assembly / mainClass := Some("com.example.Main")  // Replace with your main class

// Ensure the main class is included in the assembly JAR
assembly / mainClass := Some("com.example.Main")  // Replace with your main class

// Optional: This can make the assembly JAR more optimized.
assembly / assemblyJarName := "app.jar"
