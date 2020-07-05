package trial1

import cats.Semigroup
import cats.data.NonEmptyList
import cats.effect._
import cats.syntax.either._
import cats.syntax.semigroupk._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import sttp.tapir._
import sttp.tapir.server.http4s._
import EndpointInput.PathCapture
import scala.concurrent.ExecutionContext.global

object Main3 extends IOApp {
  val nameParam: PathCapture[String] = path[String]("name")

  val greetEP: Endpoint[Unit, Unit, String, Nothing] =
    endpoint.get.out(stringBody)

  val helloEP: Endpoint[String, Unit, String, Nothing] =
    greetEP.in("hello" / nameParam)

  val hiEP: Endpoint[String, Unit, String, Nothing] =
    greetEP.in("hi" / nameParam)

  val byeEP: Endpoint[String, Unit, String, Nothing] =
    greetEP.in("bye" / nameParam)

  def hello(name: String): IO[Either[Unit, String]] = IO {
    s"Hello, $name".asRight[Unit]
  }
  def hi(name: String): IO[Either[Unit, String]] = IO {
    s"Hi, $name".asRight[Unit]
  }
  def bye(name: String): IO[Either[Unit, String]] = IO {
    s"Bye, $name".asRight[Unit]
  }
  implicit val routesSemigroup: Semigroup[HttpRoutes[IO]] = _ combineK _

  val greetingService: HttpApp[IO] = NonEmptyList.of(
    helloEP toRoutes hello,
    hiEP    toRoutes hi,
    byeEP   toRoutes bye
  ).reduce orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(greetingService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
