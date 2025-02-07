# homework-moodeng

# flow
 - web will capture mouse movement (x,y) and send to backend
 - backend(producer) will pass this event to kafaka
 - consumer will
    - save this as historical data
    - calculate it then save as heatmap
 - web/heatmap will call to backend2 to get heatmap data to display (with cache)

# To run

 - ./infra.sh
 - http://localhost/
 - http://localhost/heatmap.html

Goals

 - [x] Create the RESTful API which support Synchonous and Asynchonus call
 - [x] Use the Cassadran as the primary database
 - [x] Redis as the cache
 - [x] Kafka as a message broker
 - [x] Containererize them into docker and be able to run locally via docker-compose


TODO:
 - [x] Learning sbt
 - [x] Learning Scala
 - [x] Learning Kotlin
 - [x] Create an app
 - [x] Create infra
 - [x] Learn Kafka
 - [x] Learn Akka
 - [ ] Learn Akka Actor
 - [x] Learn Cassandra
 - [ ] Learn Event sourcing <- no trigger, no event driven
 - [x] Learn Redis
 - [ ] Learn Schema Registry <- validate schema
 - [x] Learn Proto buff


# Tasklist

 - [] Fix pipeline
 - [] Add test
 - [] extract infra and app
 - [] seed cassandra
 - [] fix cache
 - [] fix logs
 - [] extract config
 - [] refactoring
 - [] change to log4j
 - [] add observability ->try grafana
