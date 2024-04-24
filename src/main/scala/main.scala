import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

@main
def main(host: String, port: Int): Unit = {
  println("Job Start")
  val server = Future {
    val server = new EchoServer(port)
    server.start()
  }

  val client = Future {
    new EchoClient(host, port).start()
    new EchoClient(host, port).start()
  }

  Await.result(server, Duration.Inf)
  Await.result(client, Duration.Inf)
  println("Job Finish")
}