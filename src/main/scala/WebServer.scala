import java.util.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import scala.io.StdIn


object WebServer extends Configuration {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("my-hello-timed-world-system")
    implicit val materialiser = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route =
      path("hello-timed-world") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, s"<h1>Hello Timed World: ${new Date}</h1>$secret. Port $serverPort"))
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

object Configuration {
  private val env = System.getenv("ENV")

  val config: Config = env match {
    case null => ConfigFactory.load()
    case _ => ConfigFactory.load(env)
  }
}

trait Configuration {
  private def config = Configuration.config

  lazy val secret: String = config.getString("my.secret.value")
  lazy val serverPort: String = config.getString("server.port")
}