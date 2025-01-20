import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import java.util.{Collections, Properties}
import scala.jdk.CollectionConverters._
import scala.util.{Try, Success, Failure}
import org.slf4j.LoggerFactory

object KafkaConsumerExample {

  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val bootstrapServers = "localhost:9092" // Replace with your Kafka brokers
    val topic = "mouse-activity-topic" // Replace with your topic name
    val groupId = "my-scala-consumer-group" // Replace with your consumer group ID

    val properties = new Properties()
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId)
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // Start from the beginning
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false") // Disable auto-commit
    properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100") // Limit the number of records fetched in each poll

    val consumer = new KafkaConsumer[String, String](properties)

    try {
      consumer.subscribe(Collections.singletonList(topic))

      while (true) {
        val records: ConsumerRecords[String, String] = consumer.poll(Duration.ofMillis(100))

        if (!records.isEmpty) {
          records.asScala.foreach { record =>
            logger.info(s"Consumed: Key: ${record.key()}, Value: ${record.value()}, Partition: ${record.partition()}, Offset: ${record.offset()}")
            // Process your record here
            // Example:
            try {
              processRecord(record)
            } catch {
              case e: Exception => logger.error("Error processing record", e)
            }
          }
           // Manually commit offsets after processing a batch
          consumer.commitSync()
        }
      }
    } catch {
      case e: Exception => logger.error("Error in consumer loop", e)
    } finally {
      consumer.close()
    }
  }

  // Example record processing function
  def processRecord(record: org.apache.kafka.clients.consumer.ConsumerRecord[String, String]): Try[Unit] = {
    // Your logic to process the record
    // This example just prints the value
    Try {
      println(s"Processing value: ${record.value()}")
      // Simulate some processing that might fail
      if (record.value().contains("error")) {
        throw new RuntimeException("Simulated processing error")
      }
    }
  }
}