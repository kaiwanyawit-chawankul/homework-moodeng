# SBT

## what is sbt

sbt = dotnet CLI

## build files

build.sbt
project\plugins.sbt

## package site

```
resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.mavenCentral
```

## project references

```
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
```

## basic command

sbt run
sbt clean
sbt compile
sbt assembly

## To use assembly (same as C# csproj -> project setup)
assembly meta data
help build java into one file
Excercise/Day04/backend/build.sbt

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.1") //
enablePlugins(AssemblyPlugin)

## Setup for intelliJ

command + ,
build tools -> sbt -> JRE -> java 17

## reference sites

