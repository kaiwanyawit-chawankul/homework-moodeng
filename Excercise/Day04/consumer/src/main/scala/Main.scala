import com.datastax._
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util.{Collections, Properties}
import scala.jdk.CollectionConverters._
import org.slf4j.LoggerFactory
import your.protobuf.`package`.mouse_event.MouseEvent
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import scalapb.GeneratedMessageCompanion
import scalapb.json4s.JsonFormat

object KafkaConsumerExample {

  private val logger = LoggerFactory.getLogger(getClass)

  // Cassandra session (adjust to match your configuration)
  val session = CqlSession.builder()
    .withKeyspace("your_keyspace") // Replace with your keyspace name
    .addContactPoint(java.net.InetSocketAddress.createUnresolved("localhost", 9042))
    .build()

  def main(args: Array[String]): Unit = {
    val bootstrapServers = "localhost:9092"
    val topic = "mouse-activity-topic"
    val groupId = "my-scala-consumer-group"

    val properties = new Properties()
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false")
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100")

    val consumer = new KafkaConsumer[String, String](properties)

    try {
      consumer.subscribe(Collections.singletonList(topic))

      while (true) {
        val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100))

        if (!records.isEmpty) {
          records.asScala.foreach { record =>
            logger.info(s"Consumed: Key: ${record.key()}, Value: ${record.value()}, Partition: ${record.partition()}, Offset: ${record.offset()}")
            try {
              processRecord(record)
            } catch {
              case e: Exception => logger.error("Error processing record", e)
            }
          }
          consumer.commitSync()
        }
      }
    } catch {
      case e: Exception => logger.error("Error in consumer loop", e)
    } finally {
      consumer.close()
      session.close()
    }
  }

  // Example record processing function
  def processRecord(record: org.apache.kafka.clients.consumer.ConsumerRecord[String, String]): Unit = {
    // Convert the Kafka message (JSON) into a Protobuf message (case class)
    val json = record.value()
    try {
      // Use JsonFormat to parse the JSON string into the case class

      val mouseEvent  = JsonFormat.fromJsonString[MouseEvent](json)

      // Now save the Protobuf object to Cassandra
      saveToCassandra(mouseEvent)
    } catch {
      case e: Exception => logger.error("Error deserializing JSON to Protobuf", e)
    }
  }

  // Save the Protobuf object to Cassandra
  def saveToCassandra(mouseEvent: MouseEvent): Unit = {
    // Assuming the Cassandra schema is as follows:
    // CREATE TABLE mouse_events (event_id UUID PRIMARY KEY, event_type TEXT, data LIST<TEXT>);

    val query = QueryBuilder.insertInto("mouse_events")
      .value("event_type", QueryBuilder.literal(mouseEvent.eventType))
      .value("data", QueryBuilder.literal(mouseEvent.data.map(data =>
        s"(${data.x}, ${data.y}, ${data.time})").mkString("[", ", ", "]")))
      .build()

    session.execute(query)
    logger.info(s"Saved MouseEvent: ${mouseEvent.eventType} to Cassandra")
  }
}
