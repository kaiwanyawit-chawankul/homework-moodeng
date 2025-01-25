import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.time.Duration
import java.util.{Collections, Properties, UUID}
import scala.jdk.CollectionConverters._
import org.slf4j.LoggerFactory
import your.protobuf.`package`.mouse_event.{MouseData, MouseEvent}
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.querybuilder.QueryBuilder
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.PreparedStatement
import com.google.protobuf.InvalidProtocolBufferException
//import org.apache.kafka.common.serialization.Serdes.ByteBuffer
//import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata
//import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.metadata.schema.{KeyspaceMetadata, TableMetadata}
import java.nio.ByteBuffer
import scalapb.json4s.JsonFormat

object KafkaConsumerExample {

  private val logger = LoggerFactory.getLogger(getClass)

  var CONTAINER_IP_ADDRESS = "172.22.0.2" //cassandra
  // Cassandra session (adjust to match your configuration)
  val session = CqlSession.builder()
    .withKeyspace("test_keyspace") // Replace with your keyspace name
//    .addContactPoint(java.net.InetSocketAddress.createUnresolved("localhost", 9042))
    .addContactPoint(new java.net.InetSocketAddress("localhost", 9042))
    .withLocalDatacenter("datacenter1")
    .build()

  // Define the keyspace name
  val keyspaceName = "test_keyspace"

  // Get metadata for the keyspace
  val metadata = session.getMetadata
  val keyspace : KeyspaceMetadata = metadata.getKeyspace(keyspaceName).orElseThrow(() => new RuntimeException(s"Keyspace $keyspaceName not found"))

  // Get the list of all tables in the keyspace
  val tables = keyspace.getTables
  println(s"Tables in keyspace $keyspaceName:")

      // For each table, run a SELECT * query
    tables.forEach { (tableName, tableMetadata: TableMetadata)=>
      println(s"Querying table: ${tableName}")
    }


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

    // val query = QueryBuilder.insertInto("mouse_events")
    //   .value("eventType", QueryBuilder.literal(mouseEvent.eventType))
    //   .value("data", QueryBuilder.literal(mouseEvent.data.map(data =>
    //     s"(${data.x}, ${data.y}, ${data.time})").mkString("[", ", ", "]")))
    //   .build()

    // session.execute(query)
    // var mousePositions = mouseEvent.data.asJava;

    // val statement = SimpleStatement.builder(
    //   "INSERT INTO mouse_events (eventType, eventTimestamp, data) VALUES (?, ?, ?)"
    // )
    //   .addPositionalValues(mouseEvent.eventType, mousePositions.get(0).time, mousePositions)
    //   .build()

    // session.execute(statement)


    // logger.info(s"Saved MouseEvent: ${mouseEvent.eventType} to Cassandra")

      val insertStatement = session.prepare(s"INSERT INTO mouse_events (id, eventType, data) VALUES (?, ?, ?)")
      //val selectStatement = session.prepare(s"SELECT data FROM mouse_events WHERE eventType = ?")

      // Example Protobuf data
      val mouseData1 = MouseData(x = 10, y = 20, time = System.currentTimeMillis())
      val mouseData2 = MouseData(x = 30, y = 40, time = System.currentTimeMillis() + 100)
      val eventTimeStampForMouseEvent = System.currentTimeMillis() + 100
      val mouseEvent = MouseEvent(eventType = "mousemove", data = Seq(mouseData1, mouseData2))

      // Insert data
      insertMouseEvent(session, insertStatement, mouseEvent)

      // Retrieve data
//      val retrievedEvent = retrieveMouseEvent(session, selectStatement, "mousemove")
//      retrievedEvent match {
//        case Some(event) => println(s"Retrieved Event: $event")
//        case None => println("Event not found.")
//      }
  }

def insertMouseEvent(session: CqlSession, statement: PreparedStatement, event: MouseEvent): Unit = {
    var id: UUID = UUID.randomUUID();
    val serialized = event.toByteArray
    val byteBuffer = ByteBuffer.wrap(serialized)
    val boundStatement = statement.bind(id, event.eventType, byteBuffer)
    session.execute(boundStatement)
  }

  def retrieveMouseEvent(session: CqlSession, statement: PreparedStatement, eventType: String): Option[MouseEvent] = {
    val boundStatement = statement.bind(eventType)
    val result = session.execute(boundStatement)
    val row = result.one()
    if (row != null) {
      val byteBuffer = row.getByteBuffer("data")
      if (byteBuffer != null) {
        try {
          Some(MouseEvent.parseFrom(byteBuffer.array()))
        } catch {
          case e: InvalidProtocolBufferException =>
            println(s"Error parsing Protobuf: ${e.getMessage}")
            None
        }
      } else {
        None
      }
    } else {
      None
    }
  }
}
