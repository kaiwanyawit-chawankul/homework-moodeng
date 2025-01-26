# Kafka

## What is kafka
message queue
message bus
distributed system

distributed event streaming platform

## terminology
 - Producer => A client that publishes records (data) to Kafka topics.
 - Consumer => A client that subscribes to topics and processes the feed of records.
 - Topic => A category or feed name to which records are sent by producers.
 - Broker => A Kafka server that stores and serves data.
 - Partition => A way to split a topic into multiple parts for parallel processing.
 - Consumer Group => A group of consumers that work together to consume records from a topic.

## concepts
 - Distributed and Fault-Tolerant -> replicates data across multiple brokers to ensure availability even if some brokers fail.
 - Pub/Sub model -> producers publish messages to topics, consumers subscribe to those topics to process messages.
 - High Throughput and scalability -> can handle millions messages per second. Can scale horizontally by adding more brokers.
 - Message Retention -> can config, consumers can read the messages at their own pace, messages will be kept to allowed replaying event.
 - Stream Processing -> allow process data in real time. -> need more explanation
 - Kafka Connect -> framework for integrating with external data sources like DB, data lakes, services. Many pre-built connectors.
 - Exactly-Once Semantics -> even in failures, messages will be processed once. -> how?
 - Zookeeper -> manage cluster and consensus. (Legacy) -> read more.
 - KRaft mode -> Kafka Raft Consensus Protocol -> read more.

### Exactly-Once Semantics (EOS)

#### producer
 - idempotence -> unique sequence number -> remember for retried message (from network issues)
 - config `acks=all` to wait for ack from all replicas and enable `enable.idempotence=true` -> need to see it in action

### consumer
 - transaction support -> how?
 - Atomicity (only all or none)
 - consumer offsets -> commit when a message is successfully processed,

### Kfaka Transactions
  - begin
  - send messages
  - commit or abort

### E2E -> need both to support transactional messaging
 - producer -> enable idempotence and ack
 - consumer -> commit manually, disabling auto commit
 - ensure, transaction is never lost (producer crash before it finishes) and never double-counted (consumer fails while processing the transaction)
 - limitation -> perf overhead.
 - explain

```
producer->kafka; crash before ack
producer retries
kfaka identifies the duplicate message -> not store
consumer->process message(ok)->commit offset
consumer->process message(fail)->not commit
```



## two modes

### old version with zookeeper
wurstmeister/kafka
wurstmeister/zookeeper

```
   zookeeper:
     image: wurstmeister/zookeeper
     container_name: zookeeper
     ports:
       - "2181:2181"
     networks:
       - analytics-net
   kafka:
     image: wurstmeister/kafka
     container_name: kafka
     ports:
       - "9092:9092"
     environment:
       KAFKA_ADVERTISED_LISTENERS: INSIDE://kafka:9092,OUTSIDE://localhost:9093
       KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT
       KAFKA_LISTENERS: INSIDE://0.0.0.0:9092,OUTSIDE://0.0.0.0:9093
       KAFKA_INTER_BROKER_LISTENER_NAME: INSIDE
       KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
       KAFKA_CREATE_TOPICS: "testing:1:1"
     networks:
       - analytics-net
```

### new version with KRAFT
confluentinc/cp-kafka:latest
```

  kafka:
    image: confluentinc/cp-kafka:latest
    hostname: kafka
    container_name: kafka
    ports:
      - "9092:9092"
      - "9093:9093"
    environment:
      KAFKA_KRAFT_MODE: "true"  # This enables KRaft mode in Kafka.
      KAFKA_PROCESS_ROLES: controller,broker  # Kafka acts as both broker and controller.
      KAFKA_NODE_ID: 1  # A unique ID for this Kafka instance.
      KAFKA_CONTROLLER_QUORUM_VOTERS: "1@localhost:9093"  # Defines the controller voters.
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,CONTROLLER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_LOG_DIRS: /var/lib/kafka/data  # Where Kafka stores its logs.
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"  # Kafka will automatically create topics if needed.
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1  # Since we’re running one broker, one replica is enough.
      KAFKA_LOG_RETENTION_HOURS: 168  # Keep logs for 7 days.
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0  # No delay for consumer rebalancing.
      CLUSTER_ID: "Mk3OEYBSD34fcwNTJENDM2Qk"  # A unique ID for the Kafka cluster.
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./kafka_data:/var/lib/kafka/data  # Store Kafka logs on your local machine.
    networks:
      - analytics-net
```

## Use-cases
 - Real-time analytic (e.g, fraud detection, recommendation engines).
 - Log aggregation (centralizing logs for multiple services).
 - Event-driven microservices (decoupling services and enabling asynchronous communication).
 - Data pipeline (moving data between different storage or processing systems).


## To connect
docker exec -it kafka bash

/usr/bin/kafka-topics --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

/usr/bin/kafka-console-producer --broker-list localhost:9092 --topic test-topic

/usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic test-topic --from-beginning

/usr/bin/kafka-topics --list --bootstrap-server localhost:9092