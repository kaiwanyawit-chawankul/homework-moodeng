To create a **Scala SBT application** that runs in a **cron-job style**, periodically reading configuration data and performing some action (such as interacting with a database, Kafka, etc.), you can follow the steps below.

### Steps:

1. **Create a basic Scala project** with SBT.
2. **Add necessary libraries** for configuration reading (e.g., `pureconfig`, `typesafe config`).
3. **Create a main app** that runs periodically (simulating a cron job).
4. **Configure the application** to read settings from a configuration file.

### 1. **Setting Up SBT Project**

1. **Create a new SBT project structure**:

   ```bash
   mkdir -p scala-cron-job/src/main/scala
   cd scala-cron-job
   touch build.sbt
   mkdir -p src/main/resources
   ```

2. **`build.sbt` File**:
   Your `build.sbt` file will include dependencies like **pureconfig** for reading the configuration and **akka-http** or **scala-logging** if necessary.

   ```scala
   name := "Scala Cron Job Example"

   version := "0.1"

   scalaVersion := "2.13.8"

   libraryDependencies ++= Seq(
     "com.github.pureconfig" %% "pureconfig" % "0.17.3",    // PureConfig for config handling
     "org.slf4j" % "slf4j-api" % "1.7.32",                   // SLF4J logging
     "ch.qos.logback" % "logback-classic" % "1.2.6"            // Logback for logging
   )

   // Optional: Add sbt-assembly plugin if you want to package the app
   enablePlugins(AssemblyPlugin)
   ```

3. **Add configuration file** (e.g., `src/main/resources/application.conf`).

   Example of a basic config file:

   ```hocon
   cronJobConfig {
     intervalInMinutes = 5
     taskName = "Read from config every 5 minutes"
   }
   ```

   This config file holds settings such as the cron job interval and task name.

### 2. **Main Scala Application (Cron Job)**

In the `src/main/scala` folder, create the main application file. Let's simulate a cron job with a simple scheduled task that reads configuration values.

#### `src/main/scala/Main.scala`

```scala
import pureconfig._
import pureconfig.generic.auto._
import scala.concurrent.duration._
import akka.actor.{ActorSystem, Cancellable}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.typesafe.config.ConfigFactory

object Main extends App {

  // Step 1: Load the config
  case class CronJobConfig(intervalInMinutes: Int, taskName: String)

  val cronConfig = ConfigSource.default.load[CronJobConfig] match {
    case Right(config) => config
    case Left(error)   => throw new Exception(s"Error loading config: $error")
  }

  // Step 2: Initialize the Actor System to schedule tasks
  implicit val system: ActorSystem = ActorSystem("CronJobSystem")

  // Step 3: Define a cron job that reads the config and performs an action
  def cronJobTask(): Future[Unit] = {
    // Simulate task - log the task name and interval
    Future {
      println(s"[CronJob] Running task: ${cronConfig.taskName}")
      println(s"[CronJob] Interval: ${cronConfig.intervalInMinutes} minutes")
      // Here, you can put the logic for reading data or processing it
    }
  }

  // Step 4: Schedule the cron job using akka's scheduler
  val cancellable: Cancellable = system.scheduler.scheduleWithFixedDelay(
    initialDelay = 0.seconds,          // Initial delay before starting
    delay = cronConfig.intervalInMinutes.minutes, // Repeat every n minutes
    runnable = () => {
      cronJobTask()
    }
  )

  // Keep the application running
  println(s"Starting cron job: ${cronConfig.taskName}...")
  println(s"Interval set to ${cronConfig.intervalInMinutes} minutes.")
}
```

### Key Components:

1. **Reading the Configuration**:
   - We are using the **PureConfig** library to read configuration values. This is a simple and type-safe way of reading `.conf` files.

   - The case class `CronJobConfig` maps to the configuration in `application.conf`.

2. **Scheduling the Cron Job**:
   - We are using **Akka's Scheduler** to schedule tasks. The task runs periodically based on the interval defined in the configuration file.
   - `scheduleWithFixedDelay` is used to run the task at a fixed interval.

3. **Task Execution**:
   - The `cronJobTask()` method simulates the cron job logic, which could include reading from a database, calling an API, or processing data.

4. **Logging**:
   - You can integrate **SLF4J** and **Logback** to log messages for better traceability of the cron job’s execution.

### 3. **Running the Cron Job**

1. **Start the application**: Run the application using `sbt run`.

   ```bash
   sbt run
   ```

   You should see output every few minutes (based on the interval defined in the configuration), simulating the cron job running:

   ```
   Starting cron job: Read from config every 5 minutes...
   Interval set to 5 minutes.
   [CronJob] Running task: Read from config every 5 minutes
   [CronJob] Interval: 5 minutes
   ```

2. **Running the app in the background**:
   - If you want to keep this app running as a service or as a background process, you can use `sbt-assembly` to package it as a JAR file and run it in the background or on a server.

   To generate an executable JAR:

   ```bash
   sbt assembly
   ```

   Then run the JAR:

   ```bash
   java -jar target/scala-2.13/scala-cron-job-assembly-0.1.jar
   ```

### 4. **Packaging the App with SBT Assembly**

If you need to package this app into a single executable JAR (to run it independently), you can use the **sbt-assembly** plugin.

1. **Add the assembly plugin to your `project/plugins.sbt`**:

   ```scala
   addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.10")
   ```

2. **Add the assembly settings to `build.sbt`**:

   ```scala
   enablePlugins(AssemblyPlugin)
   ```

3. **Create the JAR**:

   ```bash
   sbt assembly
   ```

4. **Run the JAR**:

   ```bash
   java -jar target/scala-2.13/scala-cron-job-assembly-0.1.jar
   ```

### Final Notes:

- This setup runs a **simple cron-like task** using Akka's scheduler in Scala. The task reads configuration values, which can be customized to handle various types of jobs.
- You can extend the task to connect to services like **Kafka**, **Cassandra**, or **Redis**, depending on your application's needs.
- Ensure that you use a **proper logging framework** like **Logback** to monitor the behavior of the cron job in a production environment.

Let me know if you need further details or assistance!


protoc --java_out=src/main/scala src/main/proto/mouse_event.proto
protoc --java_out=src/main/java your_proto_file.proto




docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' cassandra
docker exec -it cassandra bash
cqlsh
CREATE KEYSPACE test_keyspace WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
DESCRIBE KEYSPACES;



com.datastax.oss.driver.api.core.servererrors.InvalidQueryException: Undefined column name event_type in table test_keyspace.mouse_events

com.datastax.oss.driver.api.core.servererrors.InvalidQueryException: Invalid STRING constant ([(16, 357, 1737647978172), (61, 354, 1737647978188), (190, 359, 1737647978204), (395, 423, 1737647978221), (493, 470, 1737647978238), (562, 504, 1737647978254), (597, 524, 1737647978271), (613, 532, 1737647978288), (624, 537, 1737647978304), (623, 535, 1737647978474), (617, 506, 1737647978496), (617, 468, 1737647978513), (627, 408, 1737647978529), (672, 315, 1737647978546), (709, 265, 1737647978563), (765, 204, 1737647978581), (823, 154, 1737647978604), (846, 135, 1737647978622), (860, 123, 1737647978638), (868, 115, 1737647978806), (887, 115, 1737647978822), (917, 110, 1737647978839), (1006, 95, 1737647978861), (1076, 82, 1737647978878), (1117, 71, 1737647978894), (1162, 55, 1737647978911), (1192, 40, 1737647978927), (1205, 31, 1737647978944), (1218, 20, 1737647978960), (1226, 13, 1737647978984), (1230, 8, 1737647979009), (1232, 6, 1737647979025), (1233, 5, 1737647979042), (1234, 3, 1737647979059), (1235, 0, 1737647979075)]) for "data" of type frozen<list<frozen<mouse_data>>>

MouseEvent(mousemove,List(MouseData(16,357,1737647978172,UnknownFieldSet(Map())), MouseData(61,354,1737647978188,UnknownFieldSet(Map())), MouseData(190,359,1737647978204,UnknownFieldSet(Map())), MouseData(395,423,1737647978221,UnknownFieldSet(Map())), MouseData(493,470,1737647978238,UnknownFieldSet(Map())), MouseData(562,504,1737647978254,UnknownFieldSet(Map())), MouseData(597,524,1737647978271,UnknownFieldSet(Map())), MouseData(613,532,1737647978288,UnknownFieldSet(Map())), MouseData(624,537,1737647978304,UnknownFieldSet(Map())), MouseData(623,535,1737647978474,UnknownFieldSet(Map())), MouseData(617,506,1737647978496,UnknownFieldSet(Map())), MouseData(617,468,1737647978513,UnknownFieldSet(Map())), MouseData(627,408,1737647978529,UnknownFieldSet(Map())), MouseData(672,315,1737647978546,UnknownFieldSet(Map())), MouseData(709,265,1737647978563,UnknownFieldSet(Map())), MouseData(765,204,1737647978581,UnknownFieldSet(Map())), MouseData(823,154,1737647978604,UnknownFieldSet(Map())), MouseData(846,135,1737647978622,UnknownFieldSet(Map())), MouseData(860,123,1737647978638,UnknownFieldSet(Map())), MouseData(868,115,1737647978806,UnknownFieldSet(Map())), MouseData(887,115,1737647978822,UnknownFieldSet(Map())), MouseData(917,110,1737647978839,UnknownFieldSet(Map())), MouseData(1006,95,1737647978861,UnknownFieldSet(Map())), MouseData(1076,82,1737647978878,UnknownFieldSet(Map())), MouseData(1117,71,1737647978894,UnknownFieldSet(Map())), MouseData(1162,55,1737647978911,UnknownFieldSet(Map())), MouseData(1192,40,1737647978927,UnknownFieldSet(Map())), MouseData(1205,31,1737647978944,UnknownFieldSet(Map())), MouseData(1218,20,1737647978960,UnknownFieldSet(Map())), MouseData(1226,13,1737647978984,UnknownFieldSet(Map())), MouseData(1230,8,1737647979009,UnknownFieldSet(Map())), MouseData(1232,6,1737647979025,UnknownFieldSet(Map())), MouseData(1233,5,1737647979042,UnknownFieldSet(Map())), MouseData(1234,3,1737647979059,UnknownFieldSet(Map())), MouseData(1235,0,1737647979075,UnknownFieldSet(Map()))),UnknownFieldSet(Map()))


com.datastax.oss.driver.api.core.servererrors.InvalidQueryException: Cannot execute this query as it might involve data filtering and thus may have unpredictable performance. If you want to execute this query despite the performance unpredictability, use ALLOW FILTERING

because this is not PK....try using PK



scalapb.json4s.JsonFormatException: Expected an array for repeated field data of MouseEvent



