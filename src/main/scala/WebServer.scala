import java.util.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn


object WebServer {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-hello-timed-world-system")
    implicit val materialiser = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route =
      path("hello-timed-world") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Hello Timed World: ${new Date}</h1>"))
        }
      }

    val bindingFuture = Http() bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
