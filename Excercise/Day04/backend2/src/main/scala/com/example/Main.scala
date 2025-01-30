package com.example

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.{GET, OPTIONS, POST}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Main {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system") // Create ActorSystem
    implicit val executionContext = system.executionContext // Get ExecutionContext

    val route =
      concat(
        path("heatmap") {
          options { // Handle preflight OPTIONS requests
            complete(HttpResponse(StatusCodes.OK)
              .withHeaders(
                `Access-Control-Allow-Origin`.*, // Allow all origins (for development)
                `Access-Control-Allow-Methods`(POST, OPTIONS, GET), // Allow specified methods
                `Access-Control-Allow-Headers`("Content-Type"), // Allow specified headers
                `Access-Control-Allow-Headers`("Content-Type", "Cache-Control") // Add Cache-Control
              ))
          } ~
          get {
            respondWithHeaders(
              `Access-Control-Allow-Origin`.*, // Allow all origins (for development)
            ) {
              complete(StatusCodes.OK)
            }
          }
        },
      )

    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)
    // println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    // println(s"Kafka on ...$kafkaBootstrapServers")
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
