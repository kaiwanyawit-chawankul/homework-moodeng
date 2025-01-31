package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.{GET, OPTIONS, POST}
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`, `Content-Type`}
import akka.http.scaladsl.model.{ContentTypes, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import play.api.libs.json.Format.GenericFormat
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat, enrichAny}
import play.api.libs.json.Json
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites

import scala.concurrent.Future
import scala.jdk.CollectionConverters._

object Main {

  case class HeatmapPoint(x: Int, y: Int, count: Long)

  // Define the JSON format for HeatmapPoint
  object HeatmapJsonProtocol extends DefaultJsonProtocol {
    implicit val heatmapPointFormat: RootJsonFormat[HeatmapPoint] = jsonFormat3(HeatmapPoint)
  }

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
    val statement = SimpleStatement.builder("SELECT x, y, count FROM my_keyspace.heatmap_table").build()
    //val resultSetFuture = session.execute(statement).all().stream().map(List[HeatmapPoint]) // Explicit type for clarity

    //    List<UserDto > allUsers = session
    //      .execute(QueryBuilder.selectFrom(USER_TABLENAME).all().build())
    //      .all().stream().map(UserDto::new)
    //      .collect(Collectors.toList());
    null
  }

  def main(args: Array[String]): Unit = {

    // Your main route or function
    import HeatmapJsonProtocol._
    implicit val system = ActorSystem(Behaviors.empty, "my-system") // Create ActorSystem
    implicit val executionContext = system.executionContext // Get ExecutionContext

    // Cassandra session (adjust to match your configuration)
    val session = CqlSession.builder()
      .withKeyspace("test_keyspace") // Replace with your keyspace name
      .addContactPoint(new java.net.InetSocketAddress("localhost", 9042))
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

                val customJsonMap = heatmapData.map {
                  case item: HeatmapPoint => s"${item.x},${item.y}" -> item.count
                }

                // Serialize the Map to a JSON object (not an array)
                val jsonResponse = JsObject(customJsonMap.map {
                  case (key, value) => key -> JsNumber(value)
                }.toSeq: _*).prettyPrint

                //complete(heatmapData.toJson.prettyPrint)
                //var jsonResponse = customJsonMap.toJson.prettyPrint
                complete(HttpEntity(ContentTypes.`application/json`, jsonResponse))
                //complete(Json.toJson(heatmapData))
                //complete(HttpEntity(ContentTypes.`application/json`, heatmapData.toJson.prettyPrint))
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
                // Get heatmap data from Cassandra
                val heatmapFuture = getHeatmapDataAsync(session)
                onComplete(heatmapFuture) {
                  case scala.util.Success(heatmapData) =>
                    // Convert heatmapData to JSON or other format as needed
                    // For now, just return a string representation
                    complete(heatmapData.toString) // Replace with proper JSON response
                  case scala.util.Failure(ex) =>
                    complete(StatusCodes.InternalServerError -> s"Error: ${ex.getMessage}")
                }
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
}
