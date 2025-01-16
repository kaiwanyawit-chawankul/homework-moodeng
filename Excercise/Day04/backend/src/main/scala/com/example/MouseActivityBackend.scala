import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object MouseActivityBackend {

  // Case class for the mouse activity data
  case class MouseActivity(eventType: String, data: Map[String, String])

  implicit val system: ActorSystem = ActorSystem("mouse-activity-system")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  def main(args: Array[String]): Unit = {
    // Define the route for the API
    val route =
      path("mouse-activity") {
        post {
          entity(as[String]) { body =>
            decode[MouseActivity](body) match {
              case Right(activity) =>
                println(s"Received mouse activity: $activity")
                complete(HttpResponse(status = akka.http.scaladsl.model.StatusCodes.OK))
              case Left(error) =>
                println(s"Error decoding JSON: $error")
                complete(HttpResponse(status = akka.http.scaladsl.model.StatusCodes.BadRequest))
            }
          }
        }
      }

    // Bind the HTTP server to port 8080
    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println("Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()

    // Shutdown the server when done
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
