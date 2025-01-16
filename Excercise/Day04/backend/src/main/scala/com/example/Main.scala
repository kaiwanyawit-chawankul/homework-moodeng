package com.example
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.actor.ActorSystem
//import akka.actor.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import akka.event.Logging

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}

object Main {
  implicit val system: ActorSystem = ActorSystem("backend-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  //implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "backend-system")
  //implicit val executionContext: ExecutionContextExecutor = system.executionContext


  def main(args: Array[String]): Unit = {

    val log = Logging(system, getClass) // Get a logger instance

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
              complete(s"Received mouse activity: $activity")
            }
          }
        }
      },
      path("healthcheck") {
        get {
          respondWithHeaders( // Add CORS headers here as well
            `Access-Control-Allow-Origin`.*
          ) {
            complete("OK")
          }
        }
      }
    )

    val bindingFuture = Http().newServerAt("0.0.0.0", 8080).bind(route)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    // StdIn.readLine() // Wait for user input to shut down the server
    // bindingFuture.flatMap(_.unbind()) // Unbind the server when we press RETURN
    //  .onComplete(_ => system.terminate())

    // This is the key change: Don't wait for StdIn.readLine()
    // Instead, handle shutdown with a Future

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
