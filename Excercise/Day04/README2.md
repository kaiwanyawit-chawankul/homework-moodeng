index.html

Explanation:
Mouse Events: We use mousemove and click event listeners to track the mouse's position and actions.
Data Sent to Backend: Each event sends the mouse's coordinates (clientX, clientY), timestamp, and additional information like the mouse button (for clicks) in a POST request to the Scala backend.
Backend URL: The URL used is http://localhost:8080/mouse-activity, assuming the Scala backend is running on localhost and port 8080.



Explanation:
Akka HTTP Server: We set up a basic HTTP server with Akka HTTP that listens for POST requests at /mouse-activity.
JSON Handling: Using Circe for JSON encoding and decoding. The MouseActivity case class represents the structure of the incoming mouse activity data.
eventType: A string that specifies whether it was a mousemove or click.
data: A map containing the mouse position (x, y) and additional information (e.g., the mouse button for clicks).
Route Handling: The backend decodes the incoming JSON request body into the MouseActivity case class, prints it out to the console, and responds with 200 OK if successful or 400 Bad Request if there’s a problem with the data.


await fetch("http://localhost:8080/mouse-activity", {
  "headers": {
    "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
    "accept-language": "en-US,en;q=0.9",
    "cache-control": "no-cache",
    "pragma": "no-cache",
    "sec-ch-ua": "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"",
    "sec-ch-ua-mobile": "?1",
    "sec-ch-ua-platform": "\"Android\"",
    "sec-fetch-dest": "document",
    "sec-fetch-mode": "navigate",
    "sec-fetch-site": "none",
    "sec-fetch-user": "?1",
    "upgrade-insecure-requests": "1"
  },
  "referrerPolicy": "strict-origin-when-cross-origin",
  "body": "{\"eventType\":\"mock\", \"data\":{\"mock\":\"xxxx\"}}",
  "method": "POST",
  "mode": "cors",
  "credentials": "omit"
});


https://github.com/sbt/docker-sbt?tab=readme-ov-file


sbt-assembly is a very simple plugin that can be used to create jar files for your scala application. Let's look at it with an example. First of all, we need to create a simple sbt project. In the plugins.sbt , add the sbt-assembly dependency as: addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.1.0")

Different Ways To Package A Simple Scala App - GitHub


It's great you're building a mouse tracking system! Using Kafka, Redis, and Cassandra together can create a robust and scalable solution, but it's important to understand how each technology fits into the architecture. Here's a breakdown of how you might use them:

1. Kafka: The Data Pipeline Hub

Ingestion: Mouse events (clicks, movements, scrolls) are sent from the client-side (web browser, application) to Kafka topics. Kafka acts as a central nervous system, efficiently handling high-volume, real-time data streams.  
Decoupling: Kafka decouples the data producers (clients) from the consumers (processing and storage systems). This provides fault tolerance and scalability. If one consumer goes down, the data is still safely stored in Kafka.  
Stream Processing: You can use stream processing frameworks like Kafka Streams or Apache Flink to perform real-time analysis on the mouse event data within Kafka. This could include:
Aggregating click counts per page.
Detecting user behavior patterns.
Generating real-time heatmaps.
 
2. Redis: Real-time Insights and Caching

Real-time Dashboards: Store aggregated metrics (e.g., clicks per minute, active users) in Redis for very fast retrieval and display on real-time dashboards. Redis's in-memory data store makes it ideal for this purpose.
Session Tracking: Store temporary user session data related to mouse activity. This can be useful for features like:
Highlighting recently clicked elements.
Personalizing user experience based on recent interactions.
Caching: Cache frequently accessed data or pre-computed results in Redis to reduce load on Cassandra or other downstream systems.  
3. Cassandra: Long-Term Storage and Analysis

Persistent Storage: Store the raw mouse event data in Cassandra for long-term storage and historical analysis. Cassandra is well-suited for this due to its:
High write throughput.
Scalability and fault tolerance.
Ability to handle large volumes of time-series data.
Batch Analytics: Perform batch analytics on the historical data in Cassandra to identify trends, user behavior patterns, and long-term insights. This could involve:
Analyzing user journeys and navigation patterns.
Identifying areas of user confusion or frustration.
Optimizing website or application design.
Data Flow Example:

Mouse Events -> Kafka: Mouse events are sent to a Kafka topic (e.g., "mouse-events").
Kafka Streams/Flink -> Redis: A stream processing application consumes data from Kafka, aggregates metrics (e.g., clicks per page, active users), and stores the results in Redis.
Kafka Connect/Custom Consumer -> Cassandra: Data from Kafka is persisted to Cassandra for long-term storage. You can use Kafka Connect for this or write a custom consumer application.
Dashboards/Applications -> Redis: Real-time dashboards and applications query Redis to display up-to-the-second metrics.
Analytics Tools -> Cassandra: Analytics tools (e.g., Spark, Hive) query Cassandra for historical analysis.  
Choosing the Right Data:

Kafka: Raw, high-volume mouse events (for real-time processing and decoupling).
Redis: Aggregated metrics, session data, cached results (for real-time insights and performance).  
Cassandra: Raw mouse events (for long-term storage and historical analysis).
By using Kafka, Redis, and Cassandra in this way, you can create a powerful and scalable mouse tracking system that provides both real-time insights and long-term analytical capabilities.


https://github.com/conduktor/kafka-stack-docker-compose/blob/master/full-stack.yml

https://goangle.medium.com/%E0%B8%9A%E0%B8%B1%E0%B8%99%E0%B8%97%E0%B8%B6%E0%B8%81%E0%B8%81%E0%B8%B2%E0%B8%A3%E0%B9%83%E0%B8%8A%E0%B9%89%E0%B8%87%E0%B8%B2%E0%B8%99-apache-kafka-%E0%B9%81%E0%B8%9A%E0%B8%9A%E0%B9%80%E0%B8%A3%E0%B9%88%E0%B8%87%E0%B8%A3%E0%B8%B1%E0%B8%94-bde60223ff8a

uuidgen
echo -n "a366e709-d378-40f0-8c36-a6e705ac5b49" | base64




docker exec -it kafka bash

/usr/bin/kafka-topics --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

/usr/bin/kafka-console-producer --broker-list localhost:9092 --topic test-topic

/usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic test-topic --from-beginning

/usr/bin/kafka-topics --list --bootstrap-server localhost:9092


docker exec -it cassandra cqlsh

DESCRIBE TABLES;


https://www.freeformatter.com/cron-expression-generator-quartz.html


docker run --entrypoint sh <your-image-name>


file is too big
git config --global http.postBuffer 524288000