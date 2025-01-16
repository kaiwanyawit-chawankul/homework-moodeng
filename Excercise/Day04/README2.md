index.html

Explanation:
Mouse Events: We use mousemove and click event listeners to track the mouse's position and actions.
Data Sent to Backend: Each event sends the mouse's coordinates (clientX, clientY), timestamp, and additional information like the mouse button (for clicks) in a POST request to the Scala backend.
Backend URL: The URL used is http://localhost:8080/mouse-activity, assuming the Scala backend is running on localhost and port 8080.



Explanation:
Akka HTTP Server: We set up a basic HTTP server with Akka HTTP that listens for POST requests at /mouse-activity.
JSON Handling: Using Circe for JSON encoding and decoding. The MouseActivity case class represents the structure of the incoming mouse activity data.
eventType: A string that specifies whether it was a mousemove or click.
data: A map containing the mouse position (x, y) and additional information (e.g., the mouse button for clicks).
Route Handling: The backend decodes the incoming JSON request body into the MouseActivity case class, prints it out to the console, and responds with 200 OK if successful or 400 Bad Request if there’s a problem with the data.


await fetch("http://localhost:8080/mouse-activity", {
  "headers": {
    "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
    "accept-language": "en-US,en;q=0.9",
    "cache-control": "no-cache",
    "pragma": "no-cache",
    "sec-ch-ua": "\"Google Chrome\";v=\"131\", \"Chromium\";v=\"131\", \"Not_A Brand\";v=\"24\"",
    "sec-ch-ua-mobile": "?1",
    "sec-ch-ua-platform": "\"Android\"",
    "sec-fetch-dest": "document",
    "sec-fetch-mode": "navigate",
    "sec-fetch-site": "none",
    "sec-fetch-user": "?1",
    "upgrade-insecure-requests": "1"
  },
  "referrerPolicy": "strict-origin-when-cross-origin",
  "body": "{\"eventType\":\"mock\", \"data\":{\"mock\":\"xxxx\"}}",
  "method": "POST",
  "mode": "cors",
  "credentials": "omit"
});


https://github.com/sbt/docker-sbt?tab=readme-ov-file


sbt-assembly is a very simple plugin that can be used to create jar files for your scala application. Let's look at it with an example. First of all, we need to create a simple sbt project. In the plugins.sbt , add the sbt-assembly dependency as: addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.1.0")

Different Ways To Package A Simple Scala App - GitHub