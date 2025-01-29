package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContextExecutor
import akka.event.Logging

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._

import org.apache.kafka.clients.producer.{ProducerRecord, ProducerConfig}
import org.apache.kafka.common.serialization.StringSerializer
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Main {
  implicit val system: ActorSystem = ActorSystem("backend-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val log = Logging(system, getClass)

  // Kafka Configuration
  val kafkaBootstrapServers = "kafka:9092" // Replace with your Kafka broker address
  val kafkaTopic = "mouse-activity-topic" // Replace with your topic name

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(kafkaBootstrapServers)
    .withProperty(ProducerConfig.ACKS_CONFIG, "all") // Ensure all replicas receive the message

  def main(args: Array[String]): Unit = {

    val route =
      concat(
        path("mouse-activity") {
          options { // Handle preflight OPTIONS requests
            complete(HttpResponse(StatusCodes.OK)
              .withHeaders(
                `Access-Control-Allow-Origin`.*, // Allow all origins (for development)
                `Access-Control-Allow-Methods`(POST, OPTIONS, GET), // Allow specified methods
                `Access-Control-Allow-Headers`("Content-Type"), // Allow specified headers
                `Access-Control-Allow-Headers`("Content-Type", "Cache-Control") // Add Cache-Control
              ))
          } ~
          post {
            respondWithHeaders(
              `Access-Control-Allow-Origin`.*, // Allow all origins (for development)
            ) {
              entity(as[String]) { activity =>
                // Send data to Kafka
                Source.single(new ProducerRecord[String, String](kafkaTopic, activity))
                  .runWith(Producer.plainSink(producerSettings))
                  .onComplete {
                    case scala.util.Success(_) => log.info(s"Sent activity to Kafka: $activity")
                    case scala.util.Failure(e) => {
                      println(s"Failed to send activity to Kafka: ${e.getMessage}")
                      log.error(s"Failed to send activity to Kafka: ${e.getMessage}")
                      complete(StatusCodes.InternalServerError)
                    }
                  }
                complete(StatusCodes.OK) // Respond with success even if Kafka fails
              }
            }
          }
        },
        path("healthcheck") {
          get {
            respondWithHeaders(
              `Access-Control-Allow-Origin`.* // Allow all origins (for development)
            ) {
              complete("OK")
            }
          }
        }
      )

    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    println(s"Kafka on ...$kafkaBootstrapServers")
    import scala.concurrent.duration._
    import scala.concurrent.{Await, Future}
    sys.addShutdownHook{
      println("Shutting down gracefully...")
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.terminate()) // and shutdown when done
      Await.result(system.whenTerminated, 10.seconds)
      println("Shutdown complete.")
    }
  }
}
