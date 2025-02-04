package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.{GET, OPTIONS, POST}
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Content-Type`}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import spray.json.{DefaultJsonProtocol, JsNumber, JsObject, RootJsonFormat}

import scala.concurrent.ExecutionContext.Implicits.global

import scala.compat.java8.FutureConverters.CompletionStageOps
import scala.concurrent.Future
import scala.jdk.CollectionConverters._

object Main {

  case class HeatmapPoint(x: Int, y: Int, count: Long)

  def getHeatmapData(session: CqlSession): List[HeatmapPoint] = {
    val statement = SimpleStatement.builder("SELECT x, y, count FROM test_keyspace.heatmap").build()
    val resultSet = session.execute(statement) // Execute synchronously

    resultSet.all().asScala.map { row => // Convert to Scala collection and map
      val x = row.getInt("x")
      val y = row.getInt("y")
      val count = row.getLong("count")
      HeatmapPoint(x, y, count)
    }.toList // Convert to a List
  }

  def getHeatmapDataAsync(session: CqlSession): Future[List[HeatmapPoint]] = {
    val statement = SimpleStatement.builder("SELECT x, y, count FROM test_keyspace.heatmap").build()

    session.executeAsync(statement).toScala.flatMap { resultSet =>  // Use flatMap
      val rows = resultSet.currentPage().asScala.toList // Convert to Scala List
      val heatmapPoints = rows.map { row =>
        val x = row.getInt("x")       // Direct type access for better performance
        val y = row.getInt("y")
        val count = row.getLong("count")
        HeatmapPoint(x, y, count)
      }
      Future.successful(heatmapPoints) // Return the List[HeatmapPoint] wrapped in a Future
    }
  }

  def main(args: Array[String]): Unit = {

    // Your main route or function
    implicit val system = ActorSystem(Behaviors.empty, "my-system") // Create ActorSystem
    implicit val executionContext = system.executionContext // Get ExecutionContext

    // Cassandra session (adjust to match your configuration)
    val session = CqlSession.builder()
      .withKeyspace("test_keyspace") // Replace with your keyspace name
      .addContactPoint(new java.net.InetSocketAddress("cassandra", 9042))
      .withLocalDatacenter("datacenter1")
      .build()

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
              `Content-Type`(ContentTypes.`application/json`)
            ) {
              try{
                // Get heatmap data from Cassandra
                val heatmapData = getHeatmapData(session)

                val jsonResponse: String = healMapToJson(heatmapData)

                complete(HttpEntity(ContentTypes.`application/json`, jsonResponse))
              }catch {
                case e: Throwable => // Catch all other exceptions (use with extreme caution)
                  println(s"A general error occurred: ${e.getMessage}", e)
                  complete(StatusCodes.InternalServerError -> s"Error: ${e.getMessage}")
              }

            }
          }
        },
        path("heatmap2") {
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
                val heatmapDataFuture: Future[List[HeatmapPoint]] = getHeatmapDataAsync(session)

                onComplete(heatmapDataFuture) { // Use onComplete for asynchronous results
                  case scala.util.Success(heatmapPoints) =>
                    val jsonResponse: String = healMapToJson(heatmapPoints)

                    complete(HttpEntity(ContentTypes.`application/json`, jsonResponse))//omatically marshals to JSON
                  case scala.util.Failure(exception) =>
                    System.err.println(s"Error retrieving heatmap data: ${exception.getMessage}")
                    exception.printStackTrace()
                    complete(StatusCodes.InternalServerError, s"Error: ${exception.getMessage}") // Return an error response
                }
                //null
              }
            }
        }
      )

    val bindingFuture = Http().newServerAt("0.0.0.0", 8081).bind(route)
    // println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    // println(s"Kafka on ...$kafkaBootstrapServers")
    import scala.concurrent.Await
    import scala.concurrent.duration._
    sys.addShutdownHook{
      println("Shutting down gracefully...")
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.terminate()) // and shutdown when done
      Await.result(system.whenTerminated, 10.seconds)
      println("Shutdown complete.")
    }
  }

  private def healMapToJson(heatmapData: List[HeatmapPoint]) = {
    val customJsonMap = heatmapData.map {
      case item: HeatmapPoint => s"${item.x},${item.y}" -> item.count
    }

    // Serialize the Map to a JSON object (not an array)
    val jsonResponse = JsObject(customJsonMap.map {
      case (key, value) => key -> JsNumber(value)
    }.toSeq: _*).prettyPrint
    jsonResponse
  }
}
